package com.joaquin.cerdis;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Juego extends AppCompatActivity {

    //atributos de la clasae
    private ImageView imgPersonaje;
    private TextView txtPersonaje;
    private TextView txtPuntuacion;
    private Bundle datos;
    private String []palabras;
    private String palabra;
    private String palabraPrevia;
    private TextView []letras;
    private LinearLayout layoutLetras;
    private LinearLayout layoutAhorcado;
    private ConstraintLayout parentLayout;
    private Partida partida;
    private VistaAhorcado vistaAhorcado;
    private Canvas lienzo = new Canvas();
    private int puntuacionAcumulada;
    private ArrayList<String> palabrasAcertadas;
    //Almacenar valores en cambios de estado de la actividad
    private String palabraGuardada;
    private int puntuacionGuardada;
    private int vidasGuardadas;
    private ArrayList<Integer> botonesDeshabilitados;
    private ArrayList<Integer> posicionesAcertadas;
    //contadores
    private int contAciertosSeguidos = 0;
    private int contFallosSeguidos = 0;
    private int contLetras = 0;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);

        //capturamos en objetos las vistas de personaje
        imgPersonaje = (ImageView)findViewById(R.id.img_personaje_juego);
        txtPersonaje = (TextView)findViewById(R.id.txt_nombre_juego);
        txtPuntuacion = (TextView)findViewById(R.id.txt_puntuacionVidas);

        //Inicializo ArrayList de botones pulsados y de posiciones acertadas
        botonesDeshabilitados = new ArrayList<Integer>();
        posicionesAcertadas = new ArrayList<Integer>();

        //Obtenemos el bundle de datos de la intención
        datos = getIntent().getExtras();
        puntuacionAcumulada = datos.getInt("puntuacion");
        palabraPrevia = datos.getString("palabraPrevia");

        //Obtenemos el array de palabras
        palabras = getResources().getStringArray(R.array.palabras);

        //si el bundle me devuelve contenido para palabrasAcertadas
        if(datos.getSerializable("palabrasAcertadas")!=null){

            //le asigno el valor a la variable
            palabrasAcertadas = (ArrayList<String>) datos.getSerializable("palabrasAcertadas");

            for(String palabraAcertada : palabrasAcertadas){

                for(int i = 0; i < palabras.length; i++){

                    if(palabraAcertada.equals(palabras[i])){

                        palabras[i] = "";
                    }
                }

            }

        } else {

            //la inicializo y reservo memoria
            palabrasAcertadas = new ArrayList<String>();
        }

        //restauro información almacenada en bundle en cambios de estados de actividad
        if(savedInstanceState != null){

            palabraGuardada = savedInstanceState.getString("palabra");//palabra que se estaba jugando
            puntuacionGuardada = savedInstanceState.getInt("puntuacion");//puntuación

            vidasGuardadas = savedInstanceState.getInt("vidas");//vidas
            borrarVidas(vidasGuardadas);//borro los corazones según vidas

            contAciertosSeguidos = savedInstanceState.getInt("contAciertosSeguidos", contAciertosSeguidos);//aciertos seguidos
            contFallosSeguidos = savedInstanceState.getInt("contFallosSeguidos", contFallosSeguidos);//fallos seguidos
            contLetras = savedInstanceState.getInt("contLetras", contLetras);//número de letras acertadas

            botonesDeshabilitados = (ArrayList<Integer>) savedInstanceState.getSerializable("botonesDeshabilitados");//arrayList con botones ya usados
            for(Integer intBoton : botonesDeshabilitados) deshabilitarBoton((Button)findViewById(intBoton));//recorro arrayList, recupero cada botón por su id y lo deshabilito

            posicionesAcertadas = (ArrayList<Integer>) savedInstanceState.getSerializable("posicionesAcertadas");//ArrayList con posiciones acertadas

        }

        //Ajustamos background del layout según se haya ajustado en configuración
        if(datos.getInt("color") != 0){

            parentLayout = (ConstraintLayout)findViewById(R.id.layout_juego);
            int idColor = datos.getInt("color");
            parentLayout.setBackgroundColor(getResources().getColor(idColor));
        }

        //Se crea partida nueva
        partida = new Partida();

        //Si hay puntuación guardada en cambio de estado de la actividad, ajusto la puntuación a ese valor
        if(puntuacionGuardada!=0) partida.setPuntuacion(puntuacionGuardada);
        else if(puntuacionAcumulada!=0) partida.setPuntuacion(puntuacionAcumulada);//Puntos acumulados, si no hay cambio estado

        //Si hay puntuación guardada en cambio de estado de la actividad, ajusto la puntuación a ese valor
        if(vidasGuardadas!=0) partida.setVidas(vidasGuardadas);


        //pintamos vistas con los datos
        imgPersonaje.setImageResource(datos.getInt("imagenInt"));
        txtPersonaje.setText(datos.getString("alias"));
        txtPuntuacion.setText(getString(R.string.txt_nivel) + " " + (palabrasAcertadas.size() + 1) +  " | Pts: " + partida.getPuntuacion());

        //Obtenemos el layout donde irá la palabra y luego el del ahorcado
        layoutLetras = (LinearLayout) findViewById(R.id.layout_palabra);
        layoutAhorcado = (LinearLayout)findViewById(R.id.layout_ahorcado);

        //Pintamos Vista VistaAhorcado en layout para ello
        vistaAhorcado = new VistaAhorcado(this);
        layoutAhorcado.addView(vistaAhorcado);

        //Llamo método para generar la partida
        generarPartida();



    }//onCreate

    //Guardo la información que se resetearía al cambiar de estado la actividad
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("palabra", palabra);//palabra generada para adivinar
        savedInstanceState.putInt("vidas", partida.getVidas());//vidas actuales
        savedInstanceState.putInt("puntuacion", partida.getPuntuacion());//puntuación
        savedInstanceState.putInt("contAciertosSeguidos", contAciertosSeguidos);//aciertos seguidos
        savedInstanceState.putInt("contFallosSeguidos", contFallosSeguidos);//fallos seguidos
        savedInstanceState.putInt("contLetras", contLetras);//número de letras acertadas
        savedInstanceState.putSerializable("botonesDeshabilitados", botonesDeshabilitados);//arrayList con botones ya usados
        savedInstanceState.putSerializable("posicionesAcertadas", posicionesAcertadas);//ArrayList con posiciones acertadas

    }


    public class Partida{

        //atributos
        private int puntuacion;
        private int vidas;

        //constructor por defecto
        public Partida(){
            super();
            puntuacion = 0;
            vidas = 8;
        }

        //constructor puntuación acumulada o guardada por cambio de estado en la actividad
        public Partida(int puntuacion){
            super();
            this.puntuacion = puntuacion;
            vidas = 8;
        }


        public Partida(int puntuacion, int vidas){
            super();
            this.puntuacion = puntuacion;
            this.vidas = vidas;
        }


        //getters y setters
        public int getPuntuacion(){
            return puntuacion;
        }
        public void setPuntuacion(int puntuacion){
            this.puntuacion = puntuacion;
        }
        public int getVidas(){
            return vidas;
        }
        void setVidas(int vidas){
            this.vidas = vidas;
        }

    }//Partida

    public class VistaAhorcado extends View{

        //Constructor simple por defecto
        public VistaAhorcado(Context context){ super(context); }

        //Sobreescribo este método, obligatorio para poder crear mi propia vista
        @Override
        protected void onDraw(Canvas canvas){

            //Tomo el número de vidas que existen
            int numeroVidas = partida.getVidas();

            //Lo primero es crear nuestro pincel de tipo Paint
            Paint pincel = new Paint();

            //Ajusto color, tamaño y tipo de relleno
            //Para recoger el id de un color creado en colors.xml se usa lo siguiente
            int colorPoste = ContextCompat.getColor(getContext(), R.color.color_botones);
            pincel.setColor(colorPoste);
            pincel.setStrokeWidth(16);
            pincel.setStyle(Paint.Style.STROKE);//Contorno

            //Nuevo pincel
            Paint pincelFino = new Paint();
            pincelFino.setColor(Color.WHITE);
            pincelFino.setStrokeWidth(8);
            pincelFino.setStyle(Paint.Style.STROKE);//Contorno

            //Ttrazos
            Path trazo = new Path();
            Path extremidades = new Path();


            //Si quedan 0 vidas pintará de aquí en adelante
            if(numeroVidas < 1 ) {

                //Pie derecho
                extremidades.addOval(new RectF(745, 535, 785, 555), Path.Direction.CCW);
                canvas.drawPath(extremidades, pincelFino);

            }
            //Si queda 1 vida pintará de aquí en adelante
            if(numeroVidas < 2){

                //Pie izquierdo
                extremidades.addOval(new RectF(594, 535, 637,555), Path.Direction.CW);
                canvas.drawPath(extremidades, pincelFino);

            }
            //Si quedan 2 vidas pintará de aquí en adelante
            if(numeroVidas < 3){

                //Pierna derecha
                extremidades.addArc(new RectF(539, 342, 744,722), 300, 65);
                canvas.drawPath(extremidades, pincelFino);

            }
            //Si quedan 3 vidas pintará de aquí en adelante
            if(numeroVidas < 4){

                //Pierna izquierda
                extremidades.addArc(new RectF(637, 333, 862,763), 240, -60);
                canvas.drawPath(extremidades, pincelFino);

            }
            //Si quedan 4, 5, 6, 7 vidas pintará de ahí en adelante
            if(numeroVidas < 5) canvas.drawLine(692, 213, 792,363, pincelFino);//brazo derecho
            if(numeroVidas < 6) canvas.drawLine(692, 213, 592,363, pincelFino);//brazo izquierdo
            if(numeroVidas < 7) canvas.drawLine(692, 213, 692,363, pincelFino);//cuerpo
            if(numeroVidas < 8) {

                canvas.drawCircle(692, 159, 54, pincelFino);//cabeza
                //cuerda al cuello
                trazo.addArc(new RectF(638, 197, 746,217), 70, 40);
                canvas.drawPath(trazo, pincel);
                pincelFino.setStrokeWidth(4);//Ojos X X
                canvas.drawLine(665, 140, 685,160, pincelFino);// \
                canvas.drawLine(685, 140, 665,160, pincelFino);// /
                canvas.drawLine(719, 140, 699,160, pincelFino);// \
                canvas.drawLine(699, 140, 719,160, pincelFino);// /
                canvas.drawCircle(692, 185, 10, pincelFino);//boca
                /*Toast toast = Toast.makeText(getContext(), "Supuestamente dibujando cabeza...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                 */

            }


            //El objeto que se pasa como argumento en el método
            //Usamos métodos que empiezan por draw... que se les pasa como último parámetro siempre el pincel
            canvas.drawLine(300, 625, 750,625, pincel);//base
            canvas.drawLine(308, 633, 308,5, pincel);//poste
            canvas.drawLine(308, 205, 500,5, pincel);//diagonal
            canvas.drawLine(300, 5, 700,5, pincel);//top


            pincel.setStrokeWidth(12);

            //El que se añaden los parámetros left, top, right y bottom por separado es para api 21 o más
            //Se usa este que crea un objeto de tipo RectF al que le paso dichas coordenadas
            trazo.addArc(new RectF(685, 5, 705,110), 270, 148);
            canvas.drawPath(trazo, pincel);


        }//onDraw

    }//VistaAhorcado

    //Método para ir a actividad resultado final
    public void irResultadoFinal(int puntuacion, Bundle datos, boolean gana, boolean fin){

        Intent i = new Intent(this, ResultadoPartida.class);

        i.putExtra("puntuacion", puntuacion);
        i.putExtra("gana", gana);
        i.putExtra("fin", fin);
        i.putExtra("imagenInt", datos.getInt("imagenInt"));
        i.putExtra("alias", datos.getString("alias"));
        i.putExtra("palabraPrevia", palabraPrevia);
        if(gana && !fin) i.putExtra("palabrasAcertadas", palabrasAcertadas);
        //color fondo layout
        if(datos!=null) if(datos.getInt("color") != 0) i.putExtra("color", datos.getInt("color"));

        startActivity(i);
    }

    //Método para cargar la palabra a adivinar
    public void generarPartida(){

        //Compruebo si hay palabra guardada por un cabmio de estado. En caso contrario, genero palabra de manera aleatoria
        if(palabraGuardada==null) palabra = palabras[new Random().nextInt(palabras.length)];
        else palabra = palabraGuardada;

        //compruebo que no haya salido antes. Si es así, genero otra
        while(palabra.equals(palabraPrevia) || palabra.equals(""))
            palabra = palabras[new Random().nextInt(palabras.length)];

        //Establezco el tamaño del array de textviews
        letras = new TextView[palabra.length()];

        //recorro la palabra y genero los textviews con cada letra
        for(int i=0;i<palabra.length();i++){

            boolean posicionAcertada = false;

            letras[i] = new TextView(this);
            letras[i].setText(palabra.valueOf(palabra.charAt(i)));
            letras[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            letras[i].setGravity(Gravity.CENTER);
            //Compruebo si la posición coincide con una posición guardada en un cambio de estado
            for(Integer posicion : posicionesAcertadas){
                if(i == posicion) {
                    posicionAcertada = true;//booleano a true
                    break;
                }
            }
            if(posicionAcertada) letras[i].setTextColor(Color.WHITE);//si existe la posición acertada, pinto en blanco
            else letras[i].setTextColor(Color.TRANSPARENT);//Si no, pinto transparente

            letras[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            letras[i].setBackgroundResource(R.drawable.bckg_letra);
            layoutLetras.addView(letras[i]);//Añado cada textView en el linear layout

        }

        //inserto en palabraPrevia la nueva palabra para la siguiente ronda
        palabraPrevia = palabra;

    }//generarPartida

    //Método para validar la letra pulsada
    public void validarLetra(View view){

        //Objeto de tipo Button para almacenar el botón pulsado
        Button boton = (Button)view;

        //Obtenemos la letra del botón
        String letra = boton.getText().toString();

        //booleano para registrar si se acertó al menos una vez
        boolean acierto = false;

        //Recorro la palabra para validar la letra pulsada
        for(int i=0;i<letras.length;i++){

            if(letra.equals(letras[i].getText().toString())){
                contAciertosSeguidos++;
                contLetras++;
                acierto = true;
                letras[i].setTextColor(Color.WHITE);
                posicionesAcertadas.add(i);
            }

        }

        //Comprueba si acertó o no. Resto vidas o aumento puntuación
        if(!acierto) {

            //resto vidas y puntos
            contFallosSeguidos++;
            partida.setVidas(partida.getVidas() - 1);
            borrarVida(partida.getVidas());//borro corazón

            //Para que la puntuación no baje de 0
            int nuevaPuntuacion = (partida.getPuntuacion() - (contFallosSeguidos*2));
            if(nuevaPuntuacion < 0) partida.setPuntuacion(0);
            else partida.setPuntuacion(nuevaPuntuacion);

            //Repinto el ahorcado
            layoutAhorcado.removeAllViews();
            //vistaAhorcado.draw(lienzo);
            layoutAhorcado.addView(vistaAhorcado);

            //reseteo contador puntos
            contAciertosSeguidos = 0;

            //si vidas es 0 game over
            if(partida.getVidas()==0) {

                //Pantalla final con resultado
                irResultadoFinal(partida.getPuntuacion(), datos, false, false);
            }


        } else {

            //actualizo puntuación
            partida.setPuntuacion(partida.getPuntuacion() + (contAciertosSeguidos * 5));

            //reseteo contador vida
            contFallosSeguidos = 0;

            //Si se completó la palabra ganó
            if (contLetras == letras.length) {

                //Añado la palabra al arraylist de palabras acertadas para no ponerla más
                palabrasAcertadas.add(palabra);

                if(palabrasAcertadas.size()==palabras.length) irResultadoFinal(partida.getPuntuacion(), datos, true, true);
                else irResultadoFinal(partida.getPuntuacion(), datos, true, false);//Pantalla final con resultado

            }

        }

        //Pinto puntuacion y vidas
        txtPuntuacion.setText(getString(R.string.txt_nivel) + " " + (palabrasAcertadas.size() + 1) +  " | Pts: " + partida.getPuntuacion());

        //Deshabilito el botón pulsado
        deshabilitarBoton(boton);

        //Añado el botón al arrayList de botones ya usados
        botonesDeshabilitados.add(boton.getId());

    }//validarLetra

    //método para deshabilitar botones usados
    public void deshabilitarBoton(Button boton){

        //deshabilito botones y le doy apariencia acorde
        boton.setBackgroundColor(0xFFCCCCCC);
        boton.setTextColor(0xFF999999);
        boton.setEnabled(false);
    }

    //Método borrar vidas
    public void borrarVida(int numVidas){

        ImageView imagenVida;

        switch (numVidas){
            case 0:
                imagenVida = (ImageView)findViewById(R.id.img_vida1);
                imagenVida.setVisibility(View.INVISIBLE);
                break;
            case 1:
                imagenVida = (ImageView)findViewById(R.id.img_vida2);
                imagenVida.setVisibility(View.INVISIBLE);
                break;
            case 2:
                imagenVida = (ImageView)findViewById(R.id.img_vida3);
                imagenVida.setVisibility(View.INVISIBLE);
                break;
            case 3:
                imagenVida = (ImageView)findViewById(R.id.img_vida4);
                imagenVida.setVisibility(View.INVISIBLE);
                break;
            case 4:
                imagenVida = (ImageView)findViewById(R.id.img_vida5);
                imagenVida.setVisibility(View.INVISIBLE);
                break;
            case 5:
                imagenVida = (ImageView)findViewById(R.id.img_vida6);
                imagenVida.setVisibility(View.INVISIBLE);
                break;
            case 6:
                imagenVida = (ImageView)findViewById(R.id.img_vida7);
                imagenVida.setVisibility(View.INVISIBLE);
                break;
            case 7:
                imagenVida = (ImageView)findViewById(R.id.img_vida8);
                imagenVida.setVisibility(View.INVISIBLE);
                break;
        }
    }

    //Método para borrar todas las vidas que haya perdido antes de un cambio de estado de la actividad
    public boolean borrarVidas(int numVidas){

        if(numVidas==8){return false;}
        else {
            borrarVida(numVidas);
            numVidas++;
            borrarVidas(numVidas);//hago recursividad
        }
        return true;

    }

}//MainActivity
