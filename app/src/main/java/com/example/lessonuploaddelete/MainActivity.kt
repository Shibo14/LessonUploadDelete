package com.example.lessonuploaddelete

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), MyAdapter.ItemInteractionListener {

    private lateinit var adapter: MyAdapter
    private val items = mutableListOf<ItemData>()

    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("UserData")

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(items, this)
        recyclerView.adapter = adapter

        collectionRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            items.clear()
            snapshot?.documents?.forEach { document ->
                val item = document.toObject(ItemData::class.java)

                item?.let {
                    items.add(it)
                }
            }
            adapter.notifyDataSetChanged()
        }
    }
    /*
    override fun onEditItem(item: ItemData) {
        val documentId = items.find { it == item }?.name

        val editDialog = AlertDialog.Builder(this)
        val editDialogView = layoutInflater.inflate(R.layout.dialog_edit, null)

        val editTextName = editDialogView.findViewById<EditText>(R.id.editTextName)
        val editTextDescription = editDialogView.findViewById<EditText>(R.id.editTextDescription)

        editTextName.setText(item.name)
        editTextDescription.setText(item.description)

        editDialog.setView(editDialogView)
        editDialog.setPositiveButton("Saqlash") { _, _ ->
            val updatedName = editTextName.text.toString()
            val updatedDescription = editTextDescription.text.toString()

            val updatedItem = ItemData(updatedName, updatedDescription)
            documentId?.let {
                collectionRef.document(it).set(updatedItem)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Malumotlar yangilandi.", Toast.LENGTH_SHORT).show()

                        // Eski qatorni o'chirish
                        items.remove(item)
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Yangilashda xatolik yuz berdi.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        editDialog.setNegativeButton("Bekor qilish") { _, _ ->
            // O'zgartirishni bekor qilganda bajariladigan amallar
        }
        val dialog = editDialog.create()
        dialog.show()
    }
*/

    override fun onEditItem(item: ItemData) {
        val editDialog = AlertDialog.Builder(this)
        val editDialogView = layoutInflater.inflate(R.layout.dialog_edit, null)

        val editTextName = editDialogView.findViewById<EditText>(R.id.editTextName)
        val editTextDescription = editDialogView.findViewById<EditText>(R.id.editTextDescription)

        editTextName.setText(item.name)
        editTextDescription.setText(item.description)

        editDialog.setView(editDialogView)
        editDialog.setPositiveButton("Saqlash") { _, _ ->
            val updatedName = editTextName.text.toString()
            val updatedDescription = editTextDescription.text.toString()

            val updatedItem = hashMapOf(
                "name" to updatedName,
                "description" to updatedDescription
            )

            collectionRef.whereEqualTo("name", item.name)
                .whereEqualTo("description", item.description)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.update(updatedItem as Map<String, Any>)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Malumotlar yangilandi.", Toast.LENGTH_SHORT).show()
   //0 1 2 3 4 5 6
                                // Malumotlarni yangilanganini ro'yxatdan olib tashlaymiz
                                val index = items.indexOf(item)
                                if (index != -1) { // Indeksni tekshirish
                                    items[index] = ItemData(updatedName, updatedDescription)
                                    adapter.notifyDataSetChanged()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Yangilashda xatolik yuz berdi.", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Yangilashda xatolik yuz berdi.", Toast.LENGTH_SHORT).show()
                }
        }
        editDialog.setNegativeButton("Bekor qilish") { _, _ ->
            // O'zgartirishni bekor qilish

        }

        val dialog = editDialog.create()
        dialog.show()
    }

    override fun onDeleteItem(item: ItemData) {
        val name = item.name
        val description = item.description

        val deleteDialog = AlertDialog.Builder(this)
        deleteDialog.setTitle("Tasdiqlang")
        deleteDialog.setMessage("Malumotni o'chirishni istaysizmi?")

        deleteDialog.setPositiveButton("Ha") { _, _ ->
            // Malumotni o'chirishni amalga oshirish
            collectionRef.whereEqualTo("name", name)
                .whereEqualTo("description", description)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Malumot o'chirildi.", Toast.LENGTH_SHORT).show()

                                // Malumot o'chirilganini ro'yxatdan olib tashlaymiz
                                items.remove(item)
                                adapter.notifyDataSetChanged()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "O'chirishda xatolik yuz berdi.", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "O'chirishda xatolik yuz berdi.", Toast.LENGTH_SHORT).show()
                }
        }
        deleteDialog.setNegativeButton("Yo'q") { _, _ ->
            // O'chirishni bekor qilish
        }

        val dialog = deleteDialog.create()
        dialog.show()
    }
}





