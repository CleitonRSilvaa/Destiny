$(document).ready(function () {
  $("#cpf, #editCpf").mask("000.000.000-00");

  $(".cpftable").each(function () {
    let cpfText = $(this).text().trim();

    // Se o CPF já tiver delimitadores, remova-os primeiro.
    cpfText = cpfText.replace(/\D/g, "");

    let cpfMasked = cpfText.replace(
      /(\d{3})(\d{3})(\d{3})(\d{2})/,
      "$1.$2.$3-$4"
    );
    $(this).text(cpfMasked);
  });
});

const BASE_URL = "http://localhost:8080",
  FormManager = {
    regexNumero: (e) => e.replace(/[^\d]+/g, ""),
    isValidEmail: (e) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(e),
    showAlert(e, t) {
      (e.textContent = t), (e.style.display = "block");
    },
    hideAlert(e) {
      (e.textContent = ""), (e.style.display = "none");
    },
    clearForm(e, t, a = {}) {
      let i = document.getElementById(t);
      e.forEach((e) => $(`#${e}`).val(a[e] || "")), this.hideAlert(i);
    },
    senhasSaoIguais(e, t, a) {
      let i = document.getElementById(e).value,
        o = document.getElementById(t).value;
      return i && o
        ? i !== o
          ? (document.getElementById(e).classList.add("alert", "alert-danger"),
            document.getElementById(t).classList.add("alert", "alert-danger"),
            !1)
          : (document
              .getElementById(e)
              .classList.remove("alert", "alert-danger"),
            document
              .getElementById(t)
              .classList.remove("alert", "alert-danger"),
            !0)
        : (this.showAlert(a, "As senhas s\xe3o obrigat\xf3rias!"), !1);
    },
    validateForm(e, t, a) {
      let i = document.getElementById(t),
        o = document.getElementById("editSenha")
          ? document.getElementById("editSenha").value.trim()
          : "",
        n = document.getElementById("editSenhaConfime")
          ? document.getElementById("editSenhaConfime").value.trim()
          : "",
        s = document.getElementById("senha")
          ? document.getElementById("senha").value.trim()
          : "",
        r = document.getElementById("senhaConfime")
          ? document.getElementById("senhaConfime").value.trim()
          : "";
      if ((o && !n) || (!o && n))
        return (
          this.showAlert(
            i,
            "Ambos os campos de senha de edi\xe7\xe3o devem ser preenchidos ou ambos devem ser deixados em branco!"
          ),
          !1
        );
      if (a && (!s || !r))
        return (
          this.showAlert(i, "Os campos de senha s\xe3o obrigat\xf3rios!"), !1
        );
      for (let d of e) {
        let l = $(`#${d}`).val().trim();
        if (("email" === d || "editEmail" === d) && !this.isValidEmail(l))
          return this.showAlert(i, "Email inv\xe1lido!"), !1;
        if (!l && !["editSenha", "editSenhaConfime"].includes(d))
          return (
            this.showAlert(i, "Todos os campos s\xe3o obrigat\xf3rios!"), !1
          );
      }
      return (o || n) &&
        !this.senhasSaoIguais("editSenha", "editSenhaConfime", i)
        ? (this.showAlert(i, "As senhas de edi\xe7\xe3o n\xe3o coincidem!"), !1)
        : a && !this.senhasSaoIguais("senha", "senhaConfime", i)
        ? (this.showAlert(i, "As senhas n\xe3o coincidem!"), !1)
        : (this.hideAlert(i), !0);
    },
  };
function openEditModal(e) {
  let t = e.closest("tr");
  ["id", "nome", "email", "cpf", "tipoConta", "status"].forEach((e) => {
    let a = `edit${e.charAt(0).toUpperCase() + e.slice(1)}`,
      i = document.getElementById(a);
    if (!i) {
      console.error(`Element with ID ${a} not found!`);
      return;
    }
    let o = t.getAttribute(`data-${e}`);
    if (null === o) {
      console.error(`Data attribute data-${e} not found on the row!`);
      return;
    }
    i.value = o;
  });

  if (
    document.getElementById("editEmail").value ===
    document.getElementById("usernameBeingEdited").value
  ) {
    document.getElementById("editTipoConta").disabled = true;
  } else {
    document.getElementById("editTipoConta").disabled = false;
  }

  let a = new bootstrap.Modal(document.getElementById("modalEdicao"));
  a.show();
}
function makeRequest(e, t, a, i) {
  console.log(a),
    $.ajax({
      type: e,
      url: `http://localhost:8080${t}`,
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
function updateStatusConta(e) {
  let t = e.closest("tr"),
    a = t.getAttribute("data-id"),
    i = t.getAttribute("data-status"),
    o = "ATIVA" === i ? "INATIVA" : "ATIVA",
    n = { id: a, status: o };
  Swal.fire({
    title: `${"ATIVA" === o ? "Ativar" : "Inativar"} usu\xe1rio?`,
    showCancelButton: !0,
    confirmButtonText: "Sim",
  }).then((e) => {
    e.isConfirmed &&
      makeRequest(
        "POST",
        "/usuario/updateStatus",
        n,
        "Status atualizado com sucesso!"
      );
  });
}
function getFormData(e, t = null) {
  let a = {};
  return (
    e.forEach((e) => {
      let i = t && t[e] ? t[e] : e;
      (a[i] = $(`#${e}`).val().trim()),
        e.includes("cpf") && (a[i] = FormManager.regexNumero(a[i]));
    }),
    a
  );
}
function submitForm() {
  let cpfInput = document.getElementById("cpf");
  let cpf = cpfInput.value;
  let alert = document.getElementById("cpfAlert");

  if (!validarCPF(cpf)) {
    alert.textContent = "Por favor, digite um CPF válido!";
    alert.style.display = "block";
    return;
  } else {
    alert.textContent = "";
    alert.style.display = "none";
  }

  if (
    !FormManager.validateForm(
      ["nome", "email", "senha", "senhaConfime", "cpf", "tipoConta"],
      "usuarioAlert",
      !0
    )
  )
    return;

  let e = getFormData(["nome", "email", "senha", "cpf", "tipoConta"]);
  makeRequest("POST", "/usuario/add", e, "Usu\xe1rio adicionado com sucesso!");
}
function updateUsuario() {
  let cpfInput = document.getElementById("editCpf").value;
  let alert2 = document.getElementById("cpfEditAlert");

  console.log(cpfInput, alert2);

  if (!validarCPF(cpfInput)) {
    alert2.textContent = "Por favor, digite um CPF válido!";
    alert2.style.display = "block";
    console.log("cpf invalido edit");
    return;
  } else {
    // alert.textContent = "";
    // alert.style.display = "none";
  }

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
      !1
    )
  )
    return;

  let e = getFormData(
    [
      "editId",
      "editNome",
      "editEmail",
      "editCpf",
      "editSenha",
      "editTipoConta",
      "editStatus",
    ],
    {
      editId: "id",
      editNome: "nome",
      editEmail: "email",
      editCpf: "cpf",
      editSenha: "senha",
      editTipoConta: "tipoConta",
      editStatus: "statusConta",
    }
  );
  makeRequest(
    "PUT",
    "/usuario/update",
    e,
    "Usu\xe1rio atualizado com sucesso!"
  );
}
document
  .getElementById("staticBackdrop")
  .addEventListener("hidden.bs.modal", function () {
    let alert = document.getElementById("cpfAlert");
    alert.textContent = "";
    alert.style.display = "none";

    FormManager.clearForm(
      ["nome", "email", "senha", "cpf", "senhaConfime", "tipoConta"],
      "usuarioAlert",
      { tipoConta: "ESTOQUISTA" }
    );
  }),
  document
    .getElementById("modalEdicao")
    .addEventListener("hidden.bs.modal", function () {
      let alert = document.getElementById("cpfEditAlert");
      alert.textContent = "";
      alert.style.display = "none";
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

document.addEventListener("DOMContentLoaded", function () {
  var tabela = document.querySelector("tbody");
  if (tabela.querySelectorAll("tr").length > 20) {
    tabela.classList.add("scrollable");
  }
});

$(document).ready(function () {
  var $tabela = $("tbody");
  if ($tabela.find("tr").length > 50) {
    $tabela.addClass("scrollable");
  }
});

function regexNumeroCpf(dado) {
  dado = dado.replace(/[^\d]+/g, "");
  return dado;
}

function validarCPF(cpf) {
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
}

document.getElementById("cpf").addEventListener("input", function () {
  let cpfInput = this;
  let cpf = cpfInput.value;
  let alert = document.getElementById("cpfAlert");
  if (validarCPF(cpf)) {
    cpfInput.setCustomValidity("");
    alert.textContent = "";
    alert.style.display = "none";
  } else {
    cpfInput.setCustomValidity("CPF inválido");
    alert.textContent = "Por favor, digite um CPF válido!";
    alert.style.display = "block";
  }
});

document.getElementById("editCpf").addEventListener("input", function () {
  let cpfInput = this;
  let cpf = cpfInput.value;
  let alert = document.getElementById("cpfEditAlert");

  if (validarCPF(cpf)) {
    cpfInput.setCustomValidity("");
    alert.textContent = "";
    alert.style.display = "none";
  } else {
    cpfInput.setCustomValidity("CPF inválido");
    alert.textContent = "Por favor, digite um CPF válido!";
    alert.style.display = "block";
  }
});
