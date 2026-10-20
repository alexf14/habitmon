(function () {
  "use strict";

  document.documentElement.classList.add("js");

  // Cualquier <form data-confirm="..."> pide confirmación SÍ / NO antes de enviarse.
  var dialog = document.getElementById("confirm-dialog");
  var text = document.getElementById("confirm-text");
  var pending = null;

  document.addEventListener("submit", function (e) {
    var form = e.target;
    if (!form.dataset.confirm || typeof dialog.showModal !== "function") return;
    e.preventDefault();
    pending = form;
    text.textContent = form.dataset.confirm;
    dialog.showModal();
  });

  document.getElementById("confirm-yes").addEventListener("click", function () {
    dialog.close();
    if (pending) pending.submit(); // submit() no vuelve a disparar el evento
  });
  document.getElementById("confirm-no").addEventListener("click", function () {
    pending = null;
    dialog.close();
  });
})();
