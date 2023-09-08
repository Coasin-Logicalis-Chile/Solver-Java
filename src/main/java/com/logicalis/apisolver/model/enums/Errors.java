package com.logicalis.apisolver.model.enums;

public enum Errors {
 

	dataAccessExceptionQuery {  
		@Override
		public String get(  ) {
			return "Error al realizar la consulta en la base de datos";
		}

		@Override
		public String get(String concat) {
			// TODO Auto-generated method stub
			return null;
		}
 
	},
	dataAccessExceptionUpdate {  
		@Override
		public String get() {
			return "Error al actualizar el registro en la base de datos";
		}

		@Override
		public String get(String id) {
			// TODO Auto-generated method stub
			return "Error: no se pudo editar, el registro con el id:".concat(id).concat(" no existe en la base de datos.");
		}
 
	},
	dataAccessExceptionDelete {  
		@Override
		public String get() {
			return "Error al eliminar el registro en la base de datos";
		}

		@Override
		public String get(String id) {
			// TODO Auto-generated method stub
			return "Error: no se pudo eliminar, el registro con el id:".concat(id).concat(" no existe en la base de datos.");
		}
 
	},

	dataAccessExceptionInsert {  
		@Override
		public String get() {
			return "Error al realizar la inserción en la base de datos";
		}

		@Override
		public String get(String concat) {
			// TODO Auto-generated method stub
			return null;
		}
 
	},
	responseEntity {
		public String get() {
			return "brand";
		}

		@Override
		public String get(String concat) {
			// TODO Auto-generated method stub
			return null;
		}
 
	};

	public abstract String get();
	public abstract String get(String concat);
} 