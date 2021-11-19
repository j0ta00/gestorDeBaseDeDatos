import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

import static java.sql.DriverManager.getConnection;

public class Main{
    static Scanner teclado = new Scanner(System.in);
    //PATH
    public static final String PROPERTIESFILEPATH= "configuracion.properties";
    public static Properties configuracion;
    public static void main(String[] args) {
        menuPrincipal();
    }

public static void menuPrincipal(){
            configuracion = new Properties();
            int respuesta = 0;
            String consultaOModificacion="";
            Connection miConexion;
            ResultSet resultado;
            Statement query=null;
            ResultSetMetaData rsmd;
            do {
                if(respuesta!=4) {
                    imprimirMenu();
                    respuesta = teclado.nextInt();
                    teclado.nextLine();
                }
                try(InputStream is = new FileInputStream(PROPERTIESFILEPATH)) {
                    if(respuesta!=4) {
                        configuracion.load(is);
                        miConexion = getConnection(configuracion.getProperty("URL"), configuracion.getProperty("USUARIO"), configuracion.getProperty("CLAVE"));
                        query = miConexion.createStatement();
                    }
                    realizarAccionSeleccionada(respuesta,query);
                } catch (SQLException e) {
                    respuesta = 4;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                }
            }while(respuesta!=5);
        }

        public static void realizarAccionSeleccionada(int respuesta,Statement query) throws SQLException{
        switch (respuesta) {
                case 1: {
                    opcionSelect(query);
                }
                break;
                case 2:{
                    if(optionDML(query)){
                        System.out.println("Consulta realizada con éxito");
                    } else {
                        System.out.println("Consulta fallida");
                    }
                }break;
                case 3:{
                    if(optionDDL(query)){
                        System.out.println("Consulta realizada con éxito");
                    }else{
                        System.out.println("Consulta fallida");
                    }
                }break;
                case 4: {
                    try(OutputStream os=new FileOutputStream(PROPERTIESFILEPATH)) {
                        optionModificarConexion(os);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    respuesta=0;
                }
                break;
            }

        }


    public static void imprimirMenu(){
        System.out.println("1) Ejecutar consulta");
        System.out.println("2) Ejecutar DML");
        System.out.println("3) Ejecutar DDL");
        System.out.println("4) Configuración de la conexión");
        System.out.println("5) Salir");
    }

    public static void opcionSelect(Statement query) throws SQLException {
        ResultSet resultado;
        ResultSetMetaData rsmd;
        System.out.println("Escribe alguna consulta:");
        resultado=query.executeQuery(teclado.nextLine());
        while(resultado.next()){
            rsmd=resultado.getMetaData();
            for(int i=1;i<=rsmd.getColumnCount();i++) {
                System.out.print(resultado.getString(i)+" ");
            }
            System.out.println();
        }

    }

    public static boolean optionDDL(Statement query) throws SQLException {
        String consulta="";
        System.out.println("Escribe alguna consulta:");
        consulta=teclado.nextLine();
        return query.execute(consulta);
    }


    public static boolean optionDML(Statement query)throws SQLException{
        boolean modificacionRealizada=false;
        String consulta="";
        System.out.println("Escribe alguna consulta:");
        consulta=teclado.nextLine();
        if(query.executeUpdate(consulta)!=0){
            modificacionRealizada=true;
        }
        return modificacionRealizada;
    }

    public static void optionModificarConexion(OutputStream os) throws IOException {
        os=new FileOutputStream(PROPERTIESFILEPATH);
        String puerto,database;
        System.out.println("Introduce el puerto");
        puerto=teclado.nextLine();
        System.out.println("Introduce la base de datos");
        database=teclado.nextLine();
        configuracion.setProperty("URL", construirURL(database,puerto));
        System.out.println("Introduce el usuario");
        configuracion.setProperty("USUARIO", teclado.nextLine());
        System.out.println("Introduce la contraseña");
        configuracion.setProperty("CLAVE", teclado.nextLine());
        configuracion.store(os,null);
    }


    public static String construirURL(String database,String puerto){
        StringBuilder url=new StringBuilder();
        return url.append("jdbc:sqlserver://localhost:").append(puerto).append(";").
                append("database=").append(database).toString();
    }

}
