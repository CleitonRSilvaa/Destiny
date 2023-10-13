$(document).ready(function () {
  $("#cpf").mask("000.000.000-00");
  $("#deliveryCep").mask("00000-000");
  $("#billingCep").mask("00000-000");
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
  if (e.target.value.length == 9) {
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

function alterAllInputsClass(prefix, invert = false) {
  const div = document.getElementById(`${prefix}AddressFieldsInfo`);
  const inputs = div.querySelectorAll("input");
  const pElements = div.querySelectorAll("p");

  if (invert) {
    inputs.forEach((input) => {
      input.classList.remove("has-ok");
      input.classList.add("has-error");
    });
    return;
  }
  pElements.forEach((p) => {
    p.remove();
  });
  inputs.forEach((input) => {
    if (input.value.trim()) {
      input.classList.remove("has-error");
      input.classList.add("has-ok");
    }
  });
}

const fetchAddress = (prefix) => {
  let cep = document.getElementById(`${prefix}Cep`);
  let addressFields = document.getElementById(`${prefix}AddressFieldsInfo`);
  const existingError = cep.nextElementSibling;
  if (existingError && existingError.classList.contains("error-message")) {
    existingError.textContent = "";
  }
  fetch(`https://viacep.com.br/ws/${cep.value}/json/`)
    .then((response) => response.json())
    .then((data) => {
      if (data.erro) {
        alterAllInputsClass(prefix, true);
        addressFields.classList.remove("hidden");
        cep.classList.remove("has-error");
        alert("CEP não encontrado!");
      } else {
        document.getElementById(`${prefix}Logradouro`).value = data.logradouro;
        document.getElementById(`${prefix}Bairro`).value = data.bairro;
        document.getElementById(`${prefix}Localidade`).value = data.localidade;
        document.getElementById(`${prefix}Uf`).value = data.uf;
        cep.classList.remove("has-error");
        cep.classList.add("has-ok");
        alterAllInputsClass(prefix);
        addressFields.classList.remove("hidden");
      }
    })
    .catch(() => {
      alterAllInputsClass(prefix, true);
      addressFields.classList.remove("hidden");
      alert("Erro ao buscar o CEP!");
    });
};

function app() {
  return {
    step: 1,
    togglePassword: false,
    togglePasswordConfirm: false,
    image: "",
    password: "",
    passwordConfirm: "",

    goToNextStep() {
      if (this.validateStep(this.step)) {
        this.step++;
      }
    },

    validateStep(step) {
      console.info(step);
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
      let name = document.getElementById("name");
      let email = document.getElementById("email");
      let cpf = document.getElementById("cpf");
      let birthdate = document.getElementById("birthdate");
      let genero = document.getElementById("genero");

      let isValid = true;

      regexNumeroCpf = (dado) => {
        dado = dado.replace(/[^\d]+/g, "");
        return dado;
      };

      validarCPF = (cpf) => {
        cpf = regexNumeroCpf(cpf);

        if (cpf.length !== 11 || /^(.)\1+$/.test(cpf)) {
          return false;
        }

        let soma = 0;
        let resto;

        for (let i = 1; i <= 9; i++) {
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

        for (let j = 1; j <= 10; j++) {
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

      let namePattern = /^[A-Za-z]{3,}\s[A-Za-z]{3,}(?:\s[A-Za-z]{1,})*$/;
      let emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

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
      } else if (!emailPattern.test(email.value.trim())) {
        isValid = false;
        this.setErrorMessage(
          email,
          "Por favor, digite um e-mail válido!",
          "red"
        );
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

    validarEnderecoFatu: function () {
      let billingCep = document.getElementById("billingCep");
      let billingLogradouro = document.getElementById("billingLogradouro");
      let billingNumero = document.getElementById("billingNumero");
      let billingBairro = document.getElementById("billingBairro");
      let billingLocalidade = document.getElementById("billingLocalidade");
      let billingUf = document.getElementById("billingUf");

      let isValid = true;

      if (!billingCep.value.trim()) {
        isValid = false;
        this.setErrorMessage(billingCep, "CEP é obrigatório!");
      } else {
        this.setErrorMessage(billingCep, "");
      }

      if (!billingLogradouro.value.trim()) {
        isValid = false;
        this.setErrorMessage(billingLogradouro, "Logradouro é obrigatório!");
      } else {
        this.setErrorMessage(billingLogradouro, "");
      }

      if (!billingNumero.value.trim()) {
        isValid = false;
        this.setErrorMessage(billingNumero, "Número é obrigatório!");
      } else {
        this.setErrorMessage(billingNumero, "");
      }

      if (!billingBairro.value.trim()) {
        isValid = false;
        this.setErrorMessage(billingBairro, "Bairro é obrigatório!");
      } else {
        this.setErrorMessage(billingBairro, "");
      }

      if (!billingLocalidade.value.trim()) {
        isValid = false;
        this.setErrorMessage(billingLocalidade, "Cidade é obrigatória!");
      } else {
        this.setErrorMessage(billingLocalidade, "");
      }

      if (!billingUf.value.trim() || billingUf.value.trim().length !== 2) {
        isValid = false;
        this.setErrorMessage(
          billingUf,
          "Estado (UF) é obrigatório e deve ter 2 caracteres!"
        );
      } else {
        this.setErrorMessage(billingUf, "");
      }

      return isValid;
    },

    validateStep2: function () {
      let deliveryCep = document.getElementById("deliveryCep");
      let deliveryLogradouro = document.getElementById("deliveryLogradouro");
      let deliveryNumero = document.getElementById("deliveryNumero");
      let deliveryBairro = document.getElementById("deliveryBairro");
      let deliveryLocalidade = document.getElementById("deliveryLocalidade");
      let deliveryUf = document.getElementById("deliveryUf");
      let checkboxMesmoEndereco = document.getElementById("sameAddress");

      let isValid = true;

      if (!deliveryCep.value.trim()) {
        isValid = false;
        this.setErrorMessage(deliveryCep, "CEP é obrigatório!");
        if (!checkboxMesmoEndereco.checked) {
          this.validarEnderecoFatu();
        }
        return;
      } else {
        this.setErrorMessage(deliveryCep, "");
      }

      if (!deliveryLogradouro.value.trim()) {
        isValid = false;
        this.setErrorMessage(deliveryLogradouro, "Logradouro é obrigatório!");
      } else {
        this.setErrorMessage(deliveryLogradouro, "");
      }

      if (!deliveryNumero.value.trim()) {
        isValid = false;
        this.setErrorMessage(deliveryNumero, "Número é obrigatório!");
      } else {
        this.setErrorMessage(deliveryNumero, "");
      }

      if (!deliveryBairro.value.trim()) {
        isValid = false;
        this.setErrorMessage(deliveryBairro, "Bairro é obrigatório!");
      } else {
        this.setErrorMessage(deliveryBairro, "");
      }

      if (!deliveryLocalidade.value.trim()) {
        isValid = false;
        this.setErrorMessage(deliveryLocalidade, "Cidade é obrigatória!");
      } else {
        this.setErrorMessage(deliveryLocalidade, "");
      }

      if (!deliveryUf.value.trim() || deliveryUf.value.trim().length !== 2) {
        isValid = false;
        this.setErrorMessage(
          deliveryUf,
          "Estado (UF) é obrigatório e deve ter 2 caracteres!"
        );
      } else {
        this.setErrorMessage(deliveryUf, "");
      }

      if (!checkboxMesmoEndereco.checked) {
        isValid = this.validarEnderecoFatu();
      }

      return isValid;
    },

    validateStep3: function () {
      const password = document.getElementById("password");
      const confirmPassword = document.getElementById("passwordConfirm");

      console.log("OPAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

      let isValid = true;

      if (!password.value.trim()) {
        isValid = false;
        this.setErrorMessage(password, "Senha é obrigatório!");
      } else {
        if (password.value.trim() !== confirmPassword.value.trim()) {
          isValid = false;
          this.setErrorMessage(confirmPassword, "As senhas não coincidem!");
        } else {
          this.setErrorMessage(confirmPassword, "");
        }
      }

      return isValid;
    },
  };
}

function makeRequest(e, t, a, i) {
  console.log(a),
    $.ajax({
      type: e,
      url: `https://destinyproject.azurewebsites.net${t}`,
      data: JSON.stringify(a),
      contentType: "application/json; charset=utf-8",
      success: function (e) {
        [200, 201].includes(e.status) && "sucess" === e.message
          ? (Swal.fire({
              position: "top-end",
              icon: "success",
              title: i,
              showConfirmButton: !1,
              timer: 1900,
            }),
            setTimeout(() => location.reload(), 1900))
          : Swal.fire({
              icon: "error",
              title: "Erro",
              text: "Resposta inesperada do servidor. Por favor, tente novamente.",
            });
      },
      error: function (e) {
        let t = e.responseJSON,
          a = t.message + "\n\n";
        t.details && (a += t.details.map((e) => "- " + e).join("\n")),
          Swal.fire({ icon: "error", title: "Erro", text: a });
      },
    });
}
