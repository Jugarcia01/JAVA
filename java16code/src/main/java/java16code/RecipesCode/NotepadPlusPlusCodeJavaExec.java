package java16code.RecipesCode;

/**
 Para ejecutar esta clase de prueba desde Notepad++ se debe realizar lo siguiente:
 1. Tener instalado el plugin NppExec
 2. ir al menu Plugins -> NppExec -> Execute NppExec Script...
 3. Ingresar las siguientes sentencias:
 cd $(CURRENT_DIRECTORY)
 javac $(FILE_NAME)
 java $(NAME_PART)
 4. En la ventana emergente que aparece, dar clic en el boton OK
 5. Listo el programa se ejecutará en una ventana de NppExec Console.
 */
class NotepadPlusPlusCodeJavaExec {
	public static void main (String[] args) {
		System.out.println("Ejecutando Java desde Notepad++");
		Integer result = operation(3, 5);
		System.out.println("Result a+b=" + result);

	}

	public static Integer operation (Integer a, Integer b) {
		return a + b;
	}

}
