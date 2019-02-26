package com.lightbox.android.inpix.activities;

/**
 * Created by pablorodriguez on 11/8/18.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.lightbox.android.inpix.EditImages.EditImageFragment;
import com.lightbox.android.inpix.EditImages.FiltersListFragment;
import com.lightbox.android.inpix.R;
import com.lightbox.android.inpix.Util;
import com.lightbox.android.inpix.util.WSManager;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.lightbox.android.inpix.EditImages.BitmapUtils;

public class MessageActivity2 extends AppCompatActivity implements FiltersListFragment.FiltersListFragmentListener, EditImageFragment.EditImageFragmentListener {

    private static final String TAG = MessageActivity2.class.getSimpleName();

    public static final String IMAGE_NAME = "imgs/russia_3.jpg";

    public static final int SELECT_GALLERY_IMAGE = 101;

    @BindView(R.id.image_preview)
    ImageView imagePreview;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    Bitmap originalImage;
    // to backup image with filter applied
    Bitmap filteredImage;

    // the final image after applying
    // brightness, saturation, contrast
    Bitmap finalImage;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    // modified image values
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;

    // load native image filters library
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //getSupportActionBar().setTitle(getString(R.string.activities_main_cancel));
        Bundle bundle = getIntent().getExtras();
        String pathImge = bundle.getString("pathImage");
        //Log.i(TAG,"onCreate1: " + pathImge);
        loadImage(pathImge);
        //Log.i(TAG,"onCreate2: " + pathImge);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        //Log.i(TAG,"onCreate3: " + pathImge);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Log.i(TAG,"setupViewPager: ");

        // adding filter list fragment
        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        // adding edit image fragment
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        adapter.addFragment(editImageFragment, getString(R.string.tab_edit));

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls
        resetControls();

        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        imagePreview.setImageBitmap(filter.processFilter(filteredImage));

        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        // once the editing is done i.e seekbar is drag is completed,
        // apply the values on to filtered image
        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalImage = myFilter.processFilter(bitmap);
    }

    /**
     * Resets image edit controls to normal when new filter
     * is selected
     */
    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }

    //CAmbios

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // load the default image from assets on app launch
    private void loadImage(String path) {
        //originalImage = BitmapUtils.getBitmapFromAssets(this, IMAGE_NAME, 300, 300);
        FileInputStream fis = null;
        Log.i(TAG,"loadImage");
        try {
            //File location = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/images");
            File dest = new File(path);
            fis = new FileInputStream(dest);
            originalImage = BitmapFactory.decodeStream(fis);
            filtersListFragment.prepareThumbnail(originalImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        Log.i(TAG,"pathImage: LoadImage originalImage" + originalImage.getByteCount());
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imagePreview.setImageBitmap(originalImage);
        // render selected image thumbnails

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_rotate) {
             try {
				 finalImage = Util.rotate(finalImage, 270);
                 imagePreview.setImageBitmap(finalImage);
			  } catch (Exception e) {
					e.printStackTrace();
			  }
            return true;
        }

        if (id == R.id.action_save) {
            shareImagesToWall();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_GALLERY_IMAGE) {
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);

            // clear bitmap memory
            originalImage.recycle();
            finalImage.recycle();
            finalImage.recycle();

            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            imagePreview.setImageBitmap(originalImage);
            bitmap.recycle();

            // render selected image thumbnails
            filtersListFragment.prepareThumbnail(originalImage);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /* Compartir imagenes al muro */
    private void shareImagesToWall(){
        try {
            new UploadImage(this).execute(finalImage);
            this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class UploadImage extends AsyncTask<Bitmap, Void, Void> {
        private Context cnt;

        UploadImage(Context pCnt){
            Log.i("debugPOV", "UploadTask.Constructor");
            cnt = pCnt;
        }

        protected Void doInBackground(Bitmap... bitmaps) {
            if (bitmaps[0] == null){
                return null;
            }
            setProgress(0);
            Log.i("debugPOV", "UploadTask.doInBackground");
            SharedPreferences pref = getSharedPreferences("mypreferences",Context.MODE_PRIVATE);
            String vCode = pref.getString("opCode", "NADA");

            WSManager wsManager = new WSManager(cnt);
            wsManager.addImage(vCode,"no message",0,bitmaps);

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast tst = Toast.makeText(MessageActivity2.this, R.string.activities_main_img_shared, Toast.LENGTH_LONG);
            tst.setGravity(Gravity.CENTER|Gravity.CENTER,0,0);
            tst.show();

        }
    }

    // opening image in default image viewer app

    /*
        private void openImage(String path) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(path), "image/*");
            startActivity(intent);
        }

        private void openImageFromGallery() {
            Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(intent, SELECT_GALLERY_IMAGE);
                            } else {
                                Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        }

        private void saveImageToGallery() {
            Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                final String path = BitmapUtils.insertImage(getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
                                if (!TextUtils.isEmpty(path)) {
                                    Snackbar snackbar = Snackbar
                                            .make(coordinatorLayout, "Image saved to gallery!", Snackbar.LENGTH_LONG)
                                            .setAction("OPEN", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    openImage(path);
                                                }
                                            });

                                    snackbar.show();
                                } else {
                                    Snackbar snackbar = Snackbar
                                            .make(coordinatorLayout, "Unable to save image!", Snackbar.LENGTH_LONG);

                                    snackbar.show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();

        }
    */
}