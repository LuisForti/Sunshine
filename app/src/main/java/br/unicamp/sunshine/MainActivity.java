package br.unicamp.sunshine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView img;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        img = (ImageView) findViewById(R.id.imageView);
        img.setImageResource(R.drawable.unknown);
        img = (ImageView) findViewById(R.id.imageView);
        img.setImageResource(R.drawable.unknown);

        btn = (Button) findViewById(R.id.btnSearch);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Map.class);
                startActivity(intent);
                finish();
            }
        });
    }
}