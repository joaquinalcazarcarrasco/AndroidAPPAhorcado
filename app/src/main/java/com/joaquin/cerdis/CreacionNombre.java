package com.joaquin.cerdis;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CreacionNombre extends AppCompatActivity {

    //variables globales
    private ImageView imgPersonaje;
    private Bundle datos;
    private EditText nombre;
    private ConstraintLayout parentLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creacion_nombre);

        //imageview vinculada a la del xml
        imgPersonaje = (ImageView)findViewById(R.id.img_personaje);

        //Obtengo los datos enviados desde la anterior actividad
        datos = getIntent().getExtras();

        //Ajustamos background del layout según se haya ajustado en configuración
        if(datos.getInt("color") != 0){

            parentLayout = (ConstraintLayout)findViewById(R.id.layout_creacion_nombre);
            int idColor = datos.getInt("color");
            parentLayout.setBackgroundColor(getResources().getColor(idColor));
        }

        //Pinto el texto para el hint a través del bundle de datos
        nombre = (EditText)findViewById(R.id.input_nombre_personaje);
        nombre.setHint(datos.getString("personaje"));

        //Ajusto la imagen del personaje según lo enviado a través del Bundle de datos
        imgPersonaje.setImageResource(datos.getInt("imagenInt"));

        //Pongo el foco de la "escucha" en el editText para ver qué acciones lo cambian desde el teclado
        EventoTeclado teclado = new EventoTeclado();
        nombre.setOnEditorActionListener(teclado);

    }

    /* Clase interna para manejar los eventos de teclado, en concreto la tecla check
       Implementará la interfaz TextView.OnEditorActionListener y sobreescribirá el
       método onEditorAction()
    */
    class EventoTeclado implements TextView.OnEditorActionListener{

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            //Si el id de la acción del teclado sobre el textview coincide con el id
            //de IME_ACTION_DONE (check del teclado), realizamos lo mismo que con el botón siguiente
            if(actionId==EditorInfo.IME_ACTION_DONE)
                irJuego(null);

            return false;
        }
    }

    //Método que envía a la siguiente actividad y los datos
    public void irJuego(View view){

        Intent i = new Intent(this, Juego.class);

        EditText alias = (EditText)findViewById(R.id.input_nombre_personaje);
        //Obtengo los datos enviados desde la anterior actividad
        datos = getIntent().getExtras();

        if(alias.getText().toString().trim().equals(""))
            i.putExtra("alias", alias.getHint().toString());
        else
            i.putExtra("alias", alias.getText().toString());

        i.putExtra("imagenInt", datos.getInt("imagenInt"));
        //color fondo layout
        if(datos != null) if(datos.getInt("color")!=0) i.putExtra("color", datos.getInt("color"));

        startActivity(i);

    }
}

