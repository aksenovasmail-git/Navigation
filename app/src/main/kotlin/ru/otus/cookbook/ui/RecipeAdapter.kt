package ru.otus.cookbook.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.otus.cookbook.R
import ru.otus.cookbook.data.RecipeListItem
import ru.otus.cookbook.databinding.VhRecipeCategoryBinding
import ru.otus.cookbook.databinding.VhRecipeItemBinding

class RecipeAdapter(private val onRecipeClick: (Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<RecipeListItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    companion object {
        private const val TYPE_CATEGORY = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is RecipeListItem.CategoryItem -> TYPE_CATEGORY
            is RecipeListItem.RecipeItem -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_CATEGORY) {
            CategoryViewHolder(VhRecipeCategoryBinding.inflate(inflater, parent, false))
        } else {
            RecipeViewHolder(VhRecipeItemBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is CategoryViewHolder -> holder.bind(item as RecipeListItem.CategoryItem)
            is RecipeViewHolder -> {
                val recipeItem = item as RecipeListItem.RecipeItem
                holder.bind(recipeItem)
                holder.itemView.setOnClickListener { onRecipeClick(recipeItem.id) }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class CategoryViewHolder(private val binding: VhRecipeCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecipeListItem.CategoryItem) {
            binding.headerTitle.text = item.name
        }
    }

    class RecipeViewHolder(private val binding: VhRecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecipeListItem.RecipeItem) {
            binding.recipeTitle.text = item.title
            binding.recipeDescription.text = item.description
            binding.recipeImageThumbnail.load(item.imageUrl) {
                transformations()
                crossfade(true)
                placeholder(R.drawable.ic_placeholder_shapes)
                error(R.drawable.ic_placeholder_shapes)
            }
        }
    }
}