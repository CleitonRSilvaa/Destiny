$(document).ready(function () {
  $("#cpf").mask("000.000.000-00");
  $("#editCpf").mask("000.000.000-00");
});

function regexNumero(dado) {
  dado = dado.replace(/[^\d]+/g, "");
  return dado;
}

function isValidEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

function clearForm(fields, alertId, defaultValues = {}) {
  const alert = document.getElementById(alertId);
  fields.forEach((field) => $(`#${field}`).val(defaultValues[field] || ""));
  alert.textContent = "";
  alert.style.display = "none";
}

document
  .getElementById("staticBackdrop")
  .addEventListener("hidden.bs.modal", function () {
    clearForm(
      ["nome", "email", "senha", "cpf", "senhaConfime", "tipoConta"],
      "usuarioAlert",
      { tipoConta: "ESTOQUISTA" }
    );
  });

document
  .getElementById("modalEdicao")
  .addEventListener("hidden.bs.modal", function () {
    clearForm(
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

function validateForm(fields, alertId) {
  const alert = document.getElementById(alertId);
  for (let field of fields) {

    // Pega o valor do campo
    let value = $(`#${field}`).val().trim();

    // Se for um campo de email, verifica a validade do email
    if ((field === "email" || field === "editEmail") && !isValidEmail(value)) {
        alert.textContent = "Email inválido!";
        alert.style.display = "block";
        return false;
    }

    if (field === "editSenha" || field === "editSenhaConfime" || field === "senha" || field === "senhaConfime") {
        continue;
    }

    if (!value) {
      alert.textContent = "Todos os campos são obrigatórios!";
      alert.style.display = "block";
      return false;
    }
  }

  if (fields.includes("editSenha")) {
      if (!senhasSaoIguais('editSenha', 'editSenhaConfime')) {
            alert.textContent = "As senhas não coincidem!";
            alert.style.display = "block";
            return false;
        }
  } else {

      if (!senhasSaoIguais('senha', 'senhaConfime')) {
            alert.textContent = "As senhas não coincidem!";
            alert.style.display = "block";
            return false;
        }
  }


  alert.textContent = "";
  alert.style.display = "none";
  return true;
}


function postData(type, url, data, successMessage) {
  $.ajax({
    type: type,
    url: url,
    data: JSON.stringify(data),
    contentType: "application/json; charset=utf-8",
    success: function (response) {
      if (
        [200, 201].includes(response.status) &&
        response.message === "sucess"
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

function submitForm() {
  if (
    !validateForm(
      ["nome", "email", "senha","senhaConfime", "cpf", "tipoConta"],
      "usuarioAlert"
    )
  ) {
    return;
  }
  const data = {
    nome: $("#nome").val().trim(),
    email: $("#email").val().trim(),
    cpf: cpf = regexNumero($("#cpf").val().trim()),
    senha: $("#senha").val().trim(),
    tipoConta: $("#tipoConta").val().trim(),
  };
  postData(
    "POST",
    "http://localhost:8080/usuario/add",
    data,
    "Usuário adicionado com sucesso!"
  );
}

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
      postData(
        "POST",
        "http://localhost:8080/usuario/updateStatus",
        data,
        "Status atualizado com sucesso!"
      );
    }
  });
}

function updateUsuario() {
  if (
    !validateForm(
      [
        "editId",
        "editNome",
        "editEmail",
        "editCpf",
        "editSenha",
        "editTipoConta",
        "editStatus",
      ],
      "edicaoUsuarioAlert"
    )
  ) {
    return;
  }

  const data = {
    id: $("#editId").val().trim(),
    nome: $("#editNome").val().trim(),
    email: $("#editEmail").val().trim(),
    cpf: cpf = regexNumero($("#editCpf").val().trim()),
    senha: $("#editSenha").val().trim(),
    tipoConta: $("#editTipoConta").val().trim(),
    statusConta: $("#editStatus").val().trim(),
  };

  postData(
    "PUT",
    "http://localhost:8080/usuario/update",
    data,
    "Usuário atualizado com sucesso!"
  );
}


function senhasSaoIguais(idSenha, idSenhaConfime) {
    const alert = document.getElementById("usuarioAlert");
    const senha = document.getElementById(idSenha).value;
    const senhaConfime = document.getElementById(idSenhaConfime).value;
    console.log('Senha:', senha, 'Senha Confirmação:', senhaConfime); // Depuração
    if(idSenha ==="senha"){
       let value = senha.trim();
       if (!value) {
             alert.textContent = "As senhas são obrigatorias!";
             alert.style.display = "block";
             document.getElementById(idSenha).classList.add("alert", "alert-danger");
             document.getElementById(idSenhaConfime).classList.add("alert", "alert-danger");
             return false;
           }
    }
    if (senha !== senhaConfime) {
        document.getElementById(idSenha).classList.add("alert", "alert-danger");
        document.getElementById(idSenhaConfime).classList.add("alert", "alert-danger");
        return false;
    } else {
        document.getElementById(idSenha).classList.remove("alert", "alert-danger");
        document.getElementById(idSenhaConfime).classList.remove("alert", "alert-danger");
        return true;
    }
}


