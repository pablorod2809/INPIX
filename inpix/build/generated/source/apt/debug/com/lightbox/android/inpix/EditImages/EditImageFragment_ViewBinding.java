// Generated code from Butter Knife. Do not modify!
package com.lightbox.android.inpix.EditImages;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.SeekBar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lightbox.android.inpix.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class EditImageFragment_ViewBinding implements Unbinder {
  private EditImageFragment target;

  @UiThread
  public EditImageFragment_ViewBinding(EditImageFragment target, View source) {
    this.target = target;

    target.seekBarBrightness = Utils.findRequiredViewAsType(source, R.id.seekbar_brightness, "field 'seekBarBrightness'", SeekBar.class);
    target.seekBarContrast = Utils.findRequiredViewAsType(source, R.id.seekbar_contrast, "field 'seekBarContrast'", SeekBar.class);
    target.seekBarSaturation = Utils.findRequiredViewAsType(source, R.id.seekbar_saturation, "field 'seekBarSaturation'", SeekBar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    EditImageFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.seekBarBrightness = null;
    target.seekBarContrast = null;
    target.seekBarSaturation = null;
  }
}
