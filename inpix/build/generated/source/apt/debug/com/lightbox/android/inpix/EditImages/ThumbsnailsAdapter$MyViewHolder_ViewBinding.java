// Generated code from Butter Knife. Do not modify!
package com.lightbox.android.inpix.EditImages;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lightbox.android.inpix.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ThumbsnailsAdapter$MyViewHolder_ViewBinding implements Unbinder {
  private ThumbsnailsAdapter.MyViewHolder target;

  @UiThread
  public ThumbsnailsAdapter$MyViewHolder_ViewBinding(ThumbsnailsAdapter.MyViewHolder target,
      View source) {
    this.target = target;

    target.thumbnail = Utils.findRequiredViewAsType(source, R.id.thumbnail, "field 'thumbnail'", ImageView.class);
    target.filterName = Utils.findRequiredViewAsType(source, R.id.filter_name, "field 'filterName'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ThumbsnailsAdapter.MyViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.thumbnail = null;
    target.filterName = null;
  }
}
