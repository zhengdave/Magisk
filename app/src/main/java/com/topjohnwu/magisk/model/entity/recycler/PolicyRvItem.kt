package com.topjohnwu.magisk.model.entity.recycler

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.core.model.MagiskPolicy
import com.topjohnwu.magisk.databinding.ComparableRvItem
import com.topjohnwu.magisk.ktx.toggle
import com.topjohnwu.magisk.ktx.value
import com.topjohnwu.magisk.ui.superuser.SuperuserViewModel

class PolicyItem(val item: MagiskPolicy, val icon: Drawable) : ComparableRvItem<PolicyItem>() {
    override val layoutRes = R.layout.item_policy_md2

    val isExpanded = ObservableField(false)
    val isEnabled = ObservableField(item.policy == MagiskPolicy.ALLOW)
    val shouldNotify = ObservableField(item.notification)
    val shouldLog = ObservableField(item.logging)

    private val updatedPolicy
        get() = item.copy(
            policy = if (isEnabled.value) MagiskPolicy.ALLOW else MagiskPolicy.DENY,
            notification = shouldNotify.value,
            logging = shouldLog.value
        )

    fun toggle(viewModel: SuperuserViewModel) {
        if (isExpanded.value) {
            toggle()
            return
        }
        isEnabled.toggle()
        viewModel.togglePolicy(this, isEnabled.value)
    }

    fun toggle() {
        isExpanded.toggle()
    }

    fun toggleNotify(viewModel: SuperuserViewModel) {
        shouldNotify.toggle()
        viewModel.updatePolicy(updatedPolicy, isLogging = false)
    }

    fun toggleLog(viewModel: SuperuserViewModel) {
        shouldLog.toggle()
        viewModel.updatePolicy(updatedPolicy, isLogging = true)
    }

    override fun onBindingBound(binding: ViewDataBinding) {
        super.onBindingBound(binding)
        val params = binding.root.layoutParams as? StaggeredGridLayoutManager.LayoutParams
        params?.isFullSpan = true
    }

    override fun contentSameAs(other: PolicyItem) = itemSameAs(other)
    override fun itemSameAs(other: PolicyItem) = item.uid == other.item.uid

}
