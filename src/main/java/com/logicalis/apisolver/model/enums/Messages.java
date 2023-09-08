package com.logicalis.apisolver.model.enums;

public enum Messages {



	DeleteOK {  
		@Override
		public String get(  ) {
			return "¡El registro ha sido eliminado con éxito!";
		}

		@Override
		public String get(String concat) {
			// TODO Auto-generated method stub
			return null;
		}
 
	},
	UpdateOK {  
		@Override
		public String get() {
			return "¡El registro ha sido actualizado con éxito!";
		}

		@Override
		public String get(String id) {
			// TODO Auto-generated method stub
			return "Error: No se pudo editar el registro con ID:".concat(id).concat(" no existe en la base de datos.");
		}
 
	},
	createOK {  
		@Override
		public String get() {
			return "¡El registro ha sido creado con éxito!";
		}

		@Override
		public String get(String id) {
			// TODO Auto-generated method stub
			return "Error: No se pudo eliminar el registro con ID:".concat(id).concat(" no existe en la base de datos.");
		}
 
	},

	notExist {  
		@Override
		public String get() {
			return " ";
		}

		@Override
		public String get(String concat) {
			// TODO Auto-generated method stub
			return "El registro con ID: ".concat(concat).concat(" no existe en la base de datos!");
		}
 
	},
	PasswordFailed {
		@Override
		public String get() {
			return "Ya has utilizado esta contraseña. Prueba una nueva.";
		}

		@Override
		public String get(String concat) {
			// TODO Auto-generated method stub
			return "El registro con ID: ".concat(concat).concat(" no existe en la base de datos!");
		}
	};

	public abstract String get();
	public abstract String get(String concat);
}