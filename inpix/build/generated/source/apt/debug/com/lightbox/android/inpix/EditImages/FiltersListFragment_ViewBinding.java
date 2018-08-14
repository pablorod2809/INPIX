// Generated code from Butter Knife. Do not modify!
package com.lightbox.android.inpix.EditImages;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lightbox.android.inpix.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FiltersListFragment_ViewBinding implements Unbinder {
  private FiltersListFragment target;

  @UiThread
  public FiltersListFragment_ViewBinding(FiltersListFragment target, View source) {
    this.target = target;

    target.recyclerView = Utils.findRequiredViewAsType(source, R.id.recycler_view, "field 'recyclerView'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    FiltersListFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recyclerView = null;
  }
}
