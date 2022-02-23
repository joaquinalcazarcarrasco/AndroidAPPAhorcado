package com.joaquin.cerdis;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class ResultadoPartida extends AppCompatActivity {

    //variables
    private Bundle datos;
    private ImageView imgPersonaje;
    private TextView txtNombre, txtPuntuacion, txtResultado;
    private Button btnContinuar;
    private ConstraintLayout parentLayout;
    private int puntuacion;
    private String palabraPrevia;
    private ArrayList<String> palabrasAcertadas;

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultado_partida);

        //recuperamos datos del bundle de la intención
        datos = getIntent().getExtras();

        //Ajustamos background del layout según se haya ajustado en configuración
        if(datos.getInt("color") != 0){

            parentLayout = (ConstraintLayout)findViewById(R.id.layout_resultado_partida);
            int idColor = datos.getInt("color");
            parentLayout.setBackgroundColor(getResources().getColor(idColor));
        }

        //asociamos objetos a sus vistas xml
        imgPersonaje = (ImageView)findViewById(R.id.img_personaje_resultado_final);
        txtNombre = (TextView)findViewById(R.id.txt_nombre_personaje_resultado_final);
        txtResultado = (TextView)findViewById(R.id.txt_resultado_partida);
        txtPuntuacion = (TextView)findViewById(R.id.txt_puntuacion_final);
        btnContinuar = (Button)findViewById(R.id.btn_continuar_partida);

        //Asignamos contenido
        imgPersonaje.setImageResource(datos.getInt("imagenInt"));
        txtNombre.setText(datos.getString("alias"));
        txtPuntuacion.setText("Pts: " + datos.getInt("puntuacion"));

        if(datos.getBoolean("gana")) {

            if(datos.getBoolean("fin")){

                txtResultado.setText(R.string.txt_resultado_fin);
                btnContinuar.setText(R.string.btn_continuar_fin);
                puntuacion = 0;

            }else{

                txtResultado.setText(R.string.txt_resultado_gana);
                btnContinuar.setText(R.string.btn_continuar_gana);
                puntuacion = datos.getInt("puntuacion");
            }
        }
        else {
            txtResultado.setText(R.string.txt_resultado_pierde);
            btnContinuar.setText(R.string.btn_continuar_pierde);
            puntuacion = 0;
        }

        //recuperamos la última palabra
        palabraPrevia = datos.getString("palabraPrevia");
        palabrasAcertadas = (ArrayList<String>) datos.getSerializable("palabrasAcertadas");

    }//onCreate

    //Método continuar
    public void continuarPartida(View view){

        Intent i = new Intent(this, Juego.class);

        i.putExtra("puntuacion", puntuacion);
        i.putExtra("imagenInt", datos.getInt("imagenInt"));
        i.putExtra("alias", datos.getString("alias"));
        i.putExtra("palabraPrevia", palabraPrevia);
        i.putExtra("palabrasAcertadas", palabrasAcertadas);
        //color fondo Layout
        if(datos != null) if(datos.getInt("color")!=0) i.putExtra("color", datos.getInt("color"));
        startActivity(i);
    }//continuarPartida

    //método volver
    public void irInicio(View view){

        Intent i = new Intent(this, MainActivity.class);
        //color fondo Layout
        if(datos != null) if(datos.getInt("color")!=0) i.putExtra("color", datos.getInt("color"));
        startActivity(i);

    }//irInicio
}
