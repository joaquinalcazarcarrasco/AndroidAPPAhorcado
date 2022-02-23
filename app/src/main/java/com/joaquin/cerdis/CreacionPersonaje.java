package com.joaquin.cerdis;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CreacionPersonaje extends AppCompatActivity {

    //variables globales
    private Bundle datos;
    private ConstraintLayout parentLayout;

    protected void onCreate(Bundle savedIntanceState){
        super.onCreate(savedIntanceState);
        setContentView(R.layout.creacion_personaje);

        //Ajustamos background del layout según se haya ajustado en configuración
        if(getIntent().getExtras() != null){

            parentLayout = (ConstraintLayout)findViewById(R.id.layout_creacion_personaje);
            datos = getIntent().getExtras();
            int idColor = datos.getInt("color");
            parentLayout.setBackgroundColor(getResources().getColor(idColor));
        }

    }

    //Método para enviar personaje e ir a la siguiente pantalla
    public void irCreacionNombre(View view){

        //Objeto Button con el botón que se ha pulsado
        Button boton = (Button)view;
        //Intención para enviar a la siguiente actividad y enviar los datos asociados al botón
        Intent i = new Intent(this, CreacionNombre.class);

        //Obtengo id del botón
        int id = boton.getId();

        //Según el id, enviaré unos datos u otros
        switch (id){
            case R.id.btn_personaje_paloma:
                i.putExtra("personaje", getString(R.string.personaje_paloma));
                i.putExtra("imagenInt", R.drawable.icono_paloma_128);
                break;
            case R.id.btn_personaje_cerdo:
                i.putExtra("personaje", getString(R.string.personaje_cerdo));
                i.putExtra("imagenInt", R.drawable.icono_cerdo_128);
                break;
            case R.id.btn_personaje_perro:
                i.putExtra("personaje", getString(R.string.personaje_perro));
                i.putExtra("imagenInt", R.drawable.icono_perro_128);
                break;
            case R.id.btn_personaje_vaca:
                i.putExtra("personaje", getString(R.string.personaje_vaca));
                i.putExtra("imagenInt", R.drawable.icono_vaca_128);
                break;
            case R.id.btn_personaje_zorro:
                i.putExtra("personaje", getString(R.string.personaje_zorro));
                i.putExtra("imagenInt", R.drawable.icono_zorro_128);
                break;
            case R.id.btn_personaje_gato:
                i.putExtra("personaje", getString(R.string.personaje_gato));
                i.putExtra("imagenInt", R.drawable.icono_gato_128);
                break;
        }

        //color fondo layout
        if(datos != null) if(datos.getInt("color")!=0) i.putExtra("color", datos.getInt("color"));
        //Ejejcuto la intención
        startActivity(i);

    }
}
