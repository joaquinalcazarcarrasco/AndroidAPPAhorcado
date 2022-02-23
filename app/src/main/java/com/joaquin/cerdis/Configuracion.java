package com.joaquin.cerdis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Configuracion extends Activity {

    //Variables globales
    private Button botonGris, botonRosa;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracion);

        botonGris = (Button)findViewById(R.id.btn_colorGris);
        botonRosa = (Button)findViewById(R.id.btn_colorRosaOscuro);

    }

    public void ajustarColor(View view){

        int id = view.getId();
        Intent i = new Intent(this, MainActivity.class);

        if(id==botonGris.getId()) i.putExtra("color", R.color.color_fondo);
        else i.putExtra("color", R.color.color_fondo2);

        startActivity(i);


    }

}
