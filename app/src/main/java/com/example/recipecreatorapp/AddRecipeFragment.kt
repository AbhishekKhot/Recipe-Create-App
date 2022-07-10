package com.example.recipecreatorapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.recipecreatorapp.databinding.FragmentAddUpdateRecipeBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.HashMap


class AddRecipeFragment : Fragment() {
    lateinit var binding: FragmentAddUpdateRecipeBinding
    private val REQUEST_CODE = 0
    private var recipeImageUri: Uri? = null
    val storageReference = FirebaseStorage.getInstance().reference
    val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddUpdateRecipeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivRecipeImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE)
        }

        binding.btnAddRecipe.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            if (recipeImageUri != null) {
                val imageRef = storageReference.child("RecipeImages")
                    .child(FieldValue.serverTimestamp().toString() + ".jpg")
                imageRef.putFile(recipeImageUri!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        imageRef.downloadUrl.addOnSuccessListener {
                            val id = UUID.randomUUID().toString()
                            val title = binding.etRecipeTitle.text.toString()
                            val recipe = binding.etRecipe.text.toString()
                            val category = binding.etRecipeCategory.text.toString()
                            val image = it.toString()
                            saveRecipeToDatabase(id, title, recipe, category, image)
                        }
                    } else {
                        Toast.makeText(requireActivity(),
                            task.exception?.message.toString(),
                            Toast.LENGTH_SHORT).show()
                    }

                }
            } else {
                Toast.makeText(requireActivity(),
                    "Please select recipe image to post",
                    Toast.LENGTH_SHORT).show()
            }

        }
    }


    private fun saveRecipeToDatabase(
        id: String,
        title: String,
        recipe: String,
        category: String,
        image: String,
    ) {

        if (title.isNotEmpty() && recipe.isNotEmpty() && category.isNotEmpty()) {
            val recipe_data: HashMap<String, Any> = HashMap()
            recipe_data["id"] = id
            recipe_data["recipeTitle"] = title
            recipe_data["recipeProcedure"] = recipe
            recipe_data["recipeCategory"] = category
            recipe_data["recipeImage"] = image

            firestore.collection("RecipesData").document(id).set(recipe_data)
                .addOnCompleteListener {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireActivity(),
                        "Recipe posted successfully",
                        Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .addOnFailureListener {
                    Toast.makeText(requireActivity(),
                        "Failed to post new recipe",
                        Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireActivity(), "empty fields are not allowed", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            data?.data?.let {
                recipeImageUri = it
                binding.ivRecipeImage.setImageURI(it)
            }
        }
    }

}