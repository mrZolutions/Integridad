angular
  .module('app.services')
  .service('validatorService', function() {
    this.isCedulaValid = function(cedula) {
      if (typeof(cedula) == 'string' && cedula.length == 10 && /^\d+$/.test(cedula)) {
        var digitos = cedula.split('').map(Number);
        var codigo_provincia = digitos[0] * 10 + digitos[1];
        if (codigo_provincia >= 1 && (codigo_provincia <= 24 || codigo_provincia == 30)) {
          var digito_verificador = digitos.pop();
          var digito_calculado = digitos.reduce(
            function(valorPrevio, valorActual, indice) {
              return valorPrevio - (valorActual * (2 - indice % 2)) % 9 - (valorActual == 9) * 9;
          }, 1000) % 10;
          return digito_calculado === digito_verificador;
        };
        return false;
      };
    };

    this.isRucValid = function(number) {
      var dto = number.length;
      var valor;
      var acu=0;
      for (var i=0; i<dto; i++) {
        valor = number.substring(i, i+1);
        if (valor == 0 || valor == 1 || valor == 2 || valor == 3 || valor == 4 || valor == 5 || valor == 6 || valor == 7 || valor == 8 || valor == 9) {
          acu = acu+1;
        };
      };
      if (acu == dto) {
        while (number.substring(10,13) != 001) {
          return false;
        };
        while (number.substring(0,2) > 24) {
          return false;
        };
        return true;
      } else {
        return false;
      };
    };
});
