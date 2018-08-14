// Generated code from Butter Knife. Do not modify!
package com.lightbox.android.inpix.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lightbox.android.inpix.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MessageActivity2_ViewBinding implements Unbinder {
  private MessageActivity2 target;

  @UiThread
  public MessageActivity2_ViewBinding(MessageActivity2 target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MessageActivity2_ViewBinding(MessageActivity2 target, View source) {
    this.target = target;

    target.imagePreview = Utils.findRequiredViewAsType(source, R.id.image_preview, "field 'imagePreview'", ImageView.class);
    target.tabLayout = Utils.findRequiredViewAsType(source, R.id.tabs, "field 'tabLayout'", TabLayout.class);
    target.viewPager = Utils.findRequiredViewAsType(source, R.id.viewpager, "field 'viewPager'", ViewPager.class);
    target.coordinatorLayout = Utils.findRequiredViewAsType(source, R.id.coordinator_layout, "field 'coordinatorLayout'", CoordinatorLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MessageActivity2 target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.imagePreview = null;
    target.tabLayout = null;
    target.viewPager = null;
    target.coordinatorLayout = null;
  }
}
