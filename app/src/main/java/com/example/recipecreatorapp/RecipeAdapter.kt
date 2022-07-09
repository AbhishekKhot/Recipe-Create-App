package com.example.recipecreatorapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipecreatorapp.databinding.RecipeItemBinding
import android.os.Bundle


class RecipeAdapter(val recipeList:MutableList<Recipe>):RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

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
                val bundle = Bundle()
                bundle.putString("id", recipeList[position].id)
                mView.findNavController().navigate(R.id.action_allRecipeFragment_to_addAndUpdateRecipeFragment,bundle)
            }
        }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }


}