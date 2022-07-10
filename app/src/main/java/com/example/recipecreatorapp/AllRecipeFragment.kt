package com.example.recipecreatorapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipecreatorapp.adapter.RecipeAdapter
import com.example.recipecreatorapp.databinding.FragmentAllRecipeBinding
import com.example.recipecreatorapp.model.Recipe
import com.google.firebase.firestore.FirebaseFirestore

class AllRecipeFragment : Fragment() {
    lateinit var binding: FragmentAllRecipeBinding
    private val fireStore=FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding= FragmentAllRecipeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabButton.setOnClickListener {
            findNavController().navigate(R.id.action_allRecipeFragment_to_addAndUpdateRecipeFragment)
        }
        val list = mutableListOf<Recipe>()
        val recipeAdapter = RecipeAdapter(list)

        binding.recyclerView.apply {
            this.layoutManager=LinearLayoutManager(requireActivity())
            this.adapter=recipeAdapter
        }


        fireStore.collection("RecipesData")
            .get().addOnCompleteListener {
            for(snapShot in it.result){
                val recipe= Recipe(
                    snapShot.getString("id"),
                    snapShot.getString("recipeTitle"),
                    snapShot.getString("recipeProcedure"),
                    snapShot.getString("recipeCategory"),
                    snapShot.getString("recipeImage"),
                )
                list.add(recipe)
            }
            recipeAdapter.notifyDataSetChanged()
        }
    }
}