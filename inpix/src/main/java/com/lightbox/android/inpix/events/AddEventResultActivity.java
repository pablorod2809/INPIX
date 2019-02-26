package com.lightbox.android.inpix.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.lightbox.android.inpix.R;
import com.lightbox.android.inpix.activities.EventListActivity;

public class AddEventResultActivity extends AppCompatActivity{

    private RadioGroup visibility;

    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_new_result);
        btnNext = findViewById(R.id.btnNewGroup2_Next);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextStepIntent = new Intent(AddEventResultActivity.this, EventListActivity.class);
                startActivity(nextStepIntent);

            }
        });

    }


}

