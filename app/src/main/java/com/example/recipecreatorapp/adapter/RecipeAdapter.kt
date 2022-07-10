package com.example.recipecreatorapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipecreatorapp.databinding.RecipeItemBinding
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.example.recipecreatorapp.R
import com.example.recipecreatorapp.model.Recipe
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder


class RecipeAdapter(val recipeList:MutableList<Recipe>):RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    val fireStore = FirebaseFirestore.getInstance()

    inner class RecipeViewHolder(val binding: RecipeItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = RecipeItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        with(holder){
            binding.tvTitle.text=recipe.title
            binding.tvRecipe.text=recipe.recipe
            binding.tvCategory.text=recipe.category
            Glide.with(holder.itemView).load(recipe.image).placeholder(R.drawable.ic_placeholder).into(binding.ivImage)

            binding.ivImageEdit.setOnClickListener { mView->

                val dialog = DialogPlus.newDialog(binding.ivImageEdit.context)
                    .setContentHolder(ViewHolder(R.layout.dialog_item))
                    .setExpanded(true,600)
                    .create()

                val myview: View = dialog.holderView
                val etTitle = myview.findViewById<EditText>(R.id.etTitleEdit)
                val etCategory = myview.findViewById<EditText>(R.id.etCategoryEdit)
                val etRecipe = myview.findViewById<EditText>(R.id.etRecipeEdit)
                val update = myview.findViewById<AppCompatButton>(R.id.btnUpdate)

                etTitle.hint = recipe.title
                etCategory.hint = recipe.category
                etRecipe.hint = recipe.recipe

                dialog.show()

                update.setOnClickListener { mView->
                    val updatedTitle = etTitle.text.toString()
                    val updatedCategory = etCategory.text.toString()
                    val updatedRecipe = etRecipe.text.toString()
                    val map = mutableMapOf<String, Any>()

                    if(updatedTitle.isNotEmpty()){
                        map["recipeTitle"]=updatedTitle
                    }
                    if(updatedCategory.isNotEmpty()){
                        map["recipeCategory"]=updatedCategory
                    }
                    if(updatedRecipe.isNotEmpty()){
                        map["recipeProcedure"]=updatedRecipe
                    }

                    fireStore.collection("RecipesData").document(recipe.id.toString()).update(map)
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                Snackbar.make(mView,"Recipe's updated successfully",Snackbar.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                        }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}