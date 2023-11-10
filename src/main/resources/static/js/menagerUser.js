$(document).ready(function () {
  $("#cpf, #editCpf").mask("000.000.000-00");
});

const BASE_URL = "https://destinyproject.azurewebsites.net";

const FormManager = {
  regexNumero(dado) {
    return dado.replace(/[^\d]+/g, "");
  },

  isValidEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
  },

  showAlert(alert, message) {
    alert.textContent = message;
    alert.style.display = "block";
  },

  hideAlert(alert) {
    alert.textContent = "";
    alert.style.display = "none";
  },

  clearForm(fields, alertId, defaultValues = {}) {
    const alert = document.getElementById(alertId);
    fields.forEach((field) => $(`#${field}`).val(defaultValues[field] || ""));
    this.hideAlert(alert);
  },

  senhasSaoIguais(idSenha, idSenhaConfime, alert) {
    const senha = document.getElementById(idSenha).value;
    const senhaConfime = document.getElementById(idSenhaConfime).value;

    if (!senha || !senhaConfime) {
      this.showAlert(alert, "As senhas são obrigatórias!");
      return false;
    }

    if (senha !== senhaConfime) {
      document.getElementById(idSenha).classList.add("alert", "alert-danger");
      document
        .getElementById(idSenhaConfime)
        .classList.add("alert", "alert-danger");
      return false;
    } else {
      document
        .getElementById(idSenha)
        .classList.remove("alert", "alert-danger");
      document
        .getElementById(idSenhaConfime)
        .classList.remove("alert", "alert-danger");
      return true;
    }
  },

  validateForm(fields, alertId, isCreating) {
    const alert = document.getElementById(alertId);

    // Valores dos campos de senha e edição de senha
    const editSenhaValue = document.getElementById("editSenha")
      ? document.getElementById("editSenha").value.trim()
      : "";
    const editSenhaConfimeValue = document.getElementById("editSenhaConfime")
      ? document.getElementById("editSenhaConfime").value.trim()
      : "";
    const senhaValue = document.getElementById("senha")
      ? document.getElementById("senha").value.trim()
      : "";
    const senhaConfimeValue = document.getElementById("senhaConfime")
      ? document.getElementById("senhaConfime").value.trim()
      : "";

    // Se apenas um dos campos de senha de edição estiver preenchido
    if (
      (editSenhaValue && !editSenhaConfimeValue) ||
      (!editSenhaValue && editSenhaConfimeValue)
    ) {
      this.showAlert(
        alert,
        "Ambos os campos de senha de edição devem ser preenchidos ou ambos devem ser deixados em branco!"
      );
      return false;
    }

    // Se estamos criando um novo usuário e os campos senha ou senhaConfime estiverem vazios
    if (isCreating && (!senhaValue || !senhaConfimeValue)) {
      this.showAlert(alert, "Os campos de senha são obrigatórios!");
      return false;
    }

    for (let field of fields) {
      let value = $(`#${field}`).val().trim();

      if (
        (field === "email" || field === "editEmail") &&
        !this.isValidEmail(value)
      ) {
        this.showAlert(alert, "Email inválido!");
        return false;
      }

      // Ignoramos a validação de obrigatoriedade para os campos editSenha e editSenhaConfime, pois já tratamos acima
      if (!value && !["editSenha", "editSenhaConfime"].includes(field)) {
        this.showAlert(alert, "Todos os campos são obrigatórios!");
        return false;
      }
    }

    if (editSenhaValue || editSenhaConfimeValue) {
      if (!this.senhasSaoIguais("editSenha", "editSenhaConfime", alert)) {
        this.showAlert(alert, "As senhas de edição não coincidem!");
        return false;
      }
    }

    if (isCreating && !this.senhasSaoIguais("senha", "senhaConfime", alert)) {
      this.showAlert(alert, "As senhas não coincidem!");
      return false;
    }

    this.hideAlert(alert);
    return true;
  },
};

// Event Listeners para fechar modais
document
  .getElementById("staticBackdrop")
  .addEventListener("hidden.bs.modal", function () {
    FormManager.clearForm(
      ["nome", "email", "senha", "cpf", "senhaConfime", "tipoConta"],
      "usuarioAlert",
      { tipoConta: "ESTOQUISTA" }
    );
  });

document
  .getElementById("modalEdicao")
  .addEventListener("hidden.bs.modal", function () {
    FormManager.clearForm(
      [
        "editId",
        "editNome",
        "editEmail",
        "editCpf",
        "editSenha",
        "editSenhaConfime",
        "editStatus",
        "editTipoConta",
      ],
      "edicaoUsuarioAlert"
    );
  });

function openEditModal(button) {
  const tr = button.closest("tr");

  const dataAttrs = ["id", "nome", "email", "cpf", "tipoConta", "status"];
  dataAttrs.forEach((attr) => {
    const elementId = `edit${attr.charAt(0).toUpperCase() + attr.slice(1)}`;
    const element = document.getElementById(elementId);

    if (!element) {
      console.error(`Element with ID ${elementId} not found!`);
      return;
    }

    const dataValue = tr.getAttribute(`data-${attr}`);

    if (dataValue === null) {
      console.error(`Data attribute data-${attr} not found on the row!`);
      return;
    }

    element.value = dataValue;
  });

  const modal = new bootstrap.Modal(document.getElementById("modalEdicao"));
  modal.show();
}

function makeRequest(type, url, data, successMessage) {
  console.log(data);
  $.ajax({
    type: type,
    url: `${BASE_URL}${url}`,
    data: JSON.stringify(data),
    contentType: "application/json; charset=utf-8",

    success: function (response) {
      if (
        [200, 201].includes(response.status) &&
        response.message === "success"
      ) {
        Swal.fire({
          position: "top-end",
          icon: "success",
          title: successMessage,
          showConfirmButton: false,
          timer: 1900,
        });
        setTimeout(() => location.reload(), 1900);
      } else {
        Swal.fire({
          icon: "error",
          title: "Erro",
          text: "Resposta inesperada do servidor. Por favor, tente novamente.",
        });
      }
    },
    error: function (error) {
      const response = error.responseJSON;
      let errorMessage = response.message + "\n\n";
      if (response.details) {
        errorMessage += response.details
          .map((detail) => "- " + detail)
          .join("\n");
      }

      Swal.fire({
        icon: "error",
        title: "Erro",
        text: errorMessage,
      });
    },
  });
}

function updateStatusConta(button) {
  const tr = button.closest("tr");
  const id = tr.getAttribute("data-id");
  const status = tr.getAttribute("data-status");
  const newStatus = status === "ATIVA" ? "INATIVA" : "ATIVA";
  const data = { id, status: newStatus };

  Swal.fire({
    title: `${newStatus === "ATIVA" ? "Ativar" : "Inativar"} usuário?`,
    showCancelButton: true,
    confirmButtonText: "Sim",
  }).then((result) => {
    if (result.isConfirmed) {
      makeRequest(
        "POST",
        "/usuario/updateStatus",
        data,
        "Status atualizado com sucesso!"
      );
    }
  });
}

function getFormData(fields, mapping = null) {
  let data = {};
  fields.forEach((field) => {
    const key = mapping && mapping[field] ? mapping[field] : field;

    data[key] = $(`#${field}`).val().trim();

    if (field.includes("cpf")) {
      data[key] = FormManager.regexNumero(data[key]); // Fixed method call
    }
  });
  return data;
}

function submitForm() {
  if (
    !FormManager.validateForm(
      ["nome", "email", "senha", "senhaConfime", "cpf", "tipoConta"],
      "usuarioAlert",
      true
    )
  ) {
    return;
  }
  const data = getFormData(["nome", "email", "senha", "cpf", "tipoConta"]);
  makeRequest("POST", "/usuario/add", data, "Usuário adicionado com sucesso!");
}

function updateUsuario() {
  if (
    !FormManager.validateForm(
      [
        "editId",
        "editNome",
        "editEmail",
        "editCpf",
        "editSenha",
        "editTipoConta",
        "editStatus",
      ],
      "edicaoUsuarioAlert",
      false
    )
  ) {
    return;
  }
  const mapping = {
    editId: "id",
    editNome: "nome",
    editEmail: "email",
    editCpf: "cpf",
    editSenha: "senha",
    editTipoConta: "tipoConta",
    editStatus: "statusConta",
  };
  const data = getFormData(
    [
      "editId",
      "editNome",
      "editEmail",
      "editCpf",
      "editSenha",
      "editTipoConta",
      "editStatus",
    ],
    mapping
  );
  makeRequest(
    "PUT",
    "/usuario/update",
    data,
    "Usuário atualizado com sucesso!"
  );
}
