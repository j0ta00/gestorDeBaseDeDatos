import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

import static java.sql.DriverManager.getConnection;

public class Main{
    static Scanner teclado = new Scanner(System.in);
    //PATH
    public static final String PROPERTIESFILEPATH= "configuracion.properties";
    public static void main(String[] args) {
        Properties configuracion = new Properties();
        int respuesta = 0;
        String consultaOModificacion = "";
        Connection miConexion;
        ResultSet resultado;
        Statement query=null;
        OutputStream os = null;
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
                switch (respuesta) {
                    case 1: {
                        opcionSelect(query);
                    }
                    break;
                    case 2:{
                        optionDML(query);
                    }
                    case 3:{
                        optionDDL(query);
                    }
                    case 4: {
                        os=new FileOutputStream(PROPERTIESFILEPATH);
                        System.out.println("Introduce el driver");
                        configuracion.setProperty("DRIVER", teclado.nextLine());
                        System.out.println("Introduce la URL");
                        configuracion.setProperty("URL", teclado.nextLine());
                        System.out.println("Introduce el usuario");
                        configuracion.setProperty("USUARIO", teclado.nextLine());
                        System.out.println("Introduce la contraseña");
                        configuracion.setProperty("CLAVE", teclado.nextLine());
                        configuracion.store(os,null);
                        respuesta=0;
                    }
                    break;


                }
            } catch (SQLException e) {
                respuesta = 4;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }while(respuesta!=5);
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

}
