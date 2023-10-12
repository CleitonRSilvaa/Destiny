$(document).ready(function () {
  $("#cpf").mask("000.000.000-00");
});
document.addEventListener("DOMContentLoaded", () => {
  addEventListeners();
});

const addEventListeners = () => {
  document
    .getElementById("deliveryCep")
    .addEventListener("blur", handleCEPBlur("delivery"));
  document
    .getElementById("billingCep")
    .addEventListener("blur", handleCEPBlur("billing"));
  document
    .getElementById("sameAddress")
    .addEventListener("change", handleAddressToggle);
};

const handleCEPBlur = (prefix) => (e) => {
  if (e.target.value.length > 7) {
    fetchAddress(prefix);
  }
};

const handleAddressToggle = (e) => {
  let billingFields = document.getElementById("billingAddressFields");
  if (e.target.checked) {
    billingFields.classList.add("hidden");
  } else {
    billingFields.classList.remove("hidden");
  }
};

const fetchAddress = (prefix) => {
  let cep = document.getElementById(`${prefix}Cep`).value;

  fetch(`https://viacep.com.br/ws/${cep}/json/`)
    .then((response) => response.json())
    .then((data) => {
      if (data.erro) {
        alert("CEP não encontrado!");
      } else {
        document.getElementById(`${prefix}Logradouro`).value = data.logradouro;
        document.getElementById(`${prefix}Bairro`).value = data.bairro;
        document.getElementById(`${prefix}Localidade`).value = data.localidade;
        document.getElementById(`${prefix}Uf`).value = data.uf;
      }
    })
    .catch(() => {
      alert("Erro ao buscar o CEP!");
    });
};

function app() {
  return {
    step: 1,
    passwordStrengthText: "",
    togglePassword: false,

    image: "",
    password: "",

    checkPasswordStrength() {
      var strongRegex = new RegExp(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,})"
      );
      var mediumRegex = new RegExp(
        "^(((?=.*[a-z])(?=.*[A-Z]))|((?=.*[a-z])(?=.*[0-9]))|((?=.*[A-Z])(?=.*[0-9])))(?=.{6,})"
      );

      let value = this.password;

      if (strongRegex.test(value)) {
        this.passwordStrengthText = "Strong password";
      } else if (mediumRegex.test(value)) {
        this.passwordStrengthText = "Could be stronger";
      } else {
        this.passwordStrengthText = "Too weak";
      }
    },

    goToNextStep() {
      if (this.validateStep(this.step)) {
        this.step++;
      }
    },

    validateStep(step) {
      switch (step) {
        case 1:
          return this.validateStep1();
        case 2:
          return this.validateStep2();
        case 3:
          return this.validateStep3();
        default:
          return true;
      }
    },

    setErrorMessage: (input, message) => {
      if (message) {
        input.classList.remove("has-ok");
        input.classList.add("has-error");
      } else {
        input.classList.remove("has-error");
        input.classList.add("has-ok");
      }

      const existingError = input.nextElementSibling;

      if (existingError && existingError.classList.contains("error-message")) {
        existingError.textContent = message;
      } else {
        const p = document.createElement("p");
        p.classList.add("error-message");
        p.innerText = message;
        input.insertAdjacentElement("afterend", p);
      }
    },

    validateStep1: function () {
      var name = document.getElementById("name");
      var email = document.getElementById("email");
      var cpf = document.getElementById("cpf");
      var birthdate = document.getElementById("birthdate");
      var genero = document.getElementById("genero");

      var isValid = true;

      regexNumeroCpf = (dado) => {
        dado = dado.replace(/[^\d]+/g, "");
        return dado;
      };

      validarCPF = (cpf) => {
        cpf = regexNumeroCpf(cpf);

        if (cpf.length !== 11 || /^(.)\1+$/.test(cpf)) {
          return false;
        }

        var soma = 0;
        var resto;

        for (var i = 1; i <= 9; i++) {
          soma += parseInt(cpf.substring(i - 1, i)) * (11 - i);
        }

        resto = (soma * 10) % 11;

        if (resto === 10 || resto === 11) {
          resto = 0;
        }

        if (resto !== parseInt(cpf.substring(9, 10))) {
          return false;
        }

        soma = 0;

        for (var j = 1; j <= 10; j++) {
          soma += parseInt(cpf.substring(j - 1, j)) * (12 - j);
        }

        resto = (soma * 10) % 11;

        if (resto === 10 || resto === 11) {
          resto = 0;
        }

        if (resto !== parseInt(cpf.substring(10, 11))) {
          return false;
        }

        return true;
      };

      var namePattern = /^(?:[A-Za-z]{3,}\s){1}[A-Za-z]{3,}$/;

      if (!name.value.trim()) {
        isValid = false;
        this.setErrorMessage(name, "Nome é obrigatório!", "red");
      } else if (!namePattern.test(name.value.trim())) {
        isValid = false;
        this.setErrorMessage(
          name,
          "O nome deve conter duas palavras com no mínimo 3 letras cada!",
          "red"
        );
      } else {
        this.setErrorMessage(name, "");
      }

      if (!email.value.trim()) {
        isValid = false;
        this.setErrorMessage(email, "Email é obrigatório!", "red");
      } else {
        this.setErrorMessage(email, "");
      }

      if (!cpf.value.trim()) {
        isValid = false;
        this.setErrorMessage(cpf, "CPF é obrigatório!", "red");
      } else if (!validarCPF(cpf.value.trim())) {
        isValid = false;
        this.setErrorMessage(cpf, "Por favor, digite um CPF válido!", "red");
      } else {
        this.setErrorMessage(cpf, "");
      }

      if (!birthdate.value.trim()) {
        isValid = false;
        this.setErrorMessage(
          birthdate,
          "Data de nascimento é obrigatória!",
          "red"
        );
      } else {
        const inputDate = new Date(birthdate.value);
        const today = new Date();
        const eighteenYearsAgo = new Date(
          today.getFullYear() - 18,
          today.getMonth(),
          today.getDate()
        );

        if (inputDate > today) {
          isValid = false;
          this.setErrorMessage(
            birthdate,
            "A data de nascimento não pode ser maior que a data atual!",
            "red"
          );
        } else if (inputDate > eighteenYearsAgo) {
          isValid = false;
          this.setErrorMessage(
            birthdate,
            "Você deve ter pelo menos 18 anos de idade!",
            "red"
          );
        } else {
          this.setErrorMessage(birthdate, "");
        }
      }

      if (!genero.value.trim()) {
        isValid = false;
        this.setErrorMessage(genero, "Gênero é obrigatório!", "red");
      } else {
        this.setErrorMessage(genero, "");
      }

      return isValid;
    },

    validateStep2: function () {
      return true;
    },

    validateStep3: function () {
      return true;
    },
  };
}
