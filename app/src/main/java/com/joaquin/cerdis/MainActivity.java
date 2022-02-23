package com.joaquin.cerdis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //variables globales
    private Bundle datos;
    private ConstraintLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ajustamos background del layout según se haya ajustado en configuración
        if(getIntent().getExtras() != null){

            parentLayout = (ConstraintLayout)findViewById(R.id.layout_main);
            datos = getIntent().getExtras();
            int idColor = datos.getInt("color");
            parentLayout.setBackgroundColor(getResources().getColor(idColor));
        }
    }

    //método para ir a la actividad Creación personaje
    public void irJugar(View view){
        Intent i = new Intent(this, CreacionPersonaje.class);
        //color fondo layout
        if(datos != null) if(datos.getInt("color")!=0) i.putExtra("color", datos.getInt("color"));
        startActivity(i);
    }

    //método para ir a actividad Información
    public void irInformacion(View view){
        Intent i = new Intent(this, Informacion.class);
        startActivity(i);
    }

    //método para ir a actividad Configuración
    public void irConfiguracion(View view){
        Intent i = new Intent(this, Configuracion.class);
        startActivity(i);
    }

    //Método para salir
    public void salir(View view) { finishAffinity();/*finish();*/ }

}