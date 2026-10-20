(function () {
  "use strict";

  var form = document.getElementById("starter-form");
  var radios = Array.prototype.slice.call(form.querySelectorAll('input[name="species"]'));
  var dots = form.querySelectorAll(".dots span");
  var nameEl = document.getElementById("starter-name");
  var typesEl = document.getElementById("starter-types");

  function currentIndex() {
    for (var i = 0; i < radios.length; i++) if (radios[i].checked) return i;
    return 0;
  }

  // Cada cambio actualiza sprite, nombre, tipos y el texto de confirmación.
  function render() {
    var index = currentIndex();
    var label = radios[index].parentElement;
    radios.forEach(function (r, i) {
      r.parentElement.classList.toggle("is-selected", r.checked);
      dots[i].classList.toggle("is-active", i === index);
    });
    nameEl.textContent = label.dataset.name.toUpperCase();
    typesEl.textContent = label.dataset.types;
    form.dataset.type = label.dataset.type;
    form.dataset.confirm = form.dataset.confirmTemplate.replace("{name}", label.dataset.name.toUpperCase());
  }

  function step(delta) {
    var next = (currentIndex() + delta + radios.length) % radios.length;
    radios[next].checked = true;
    render();
  }

  radios.forEach(function (r) { r.addEventListener("change", render); });

  form.querySelectorAll(".arrow").forEach(function (btn) {
    btn.addEventListener("click", function () { step(Number(btn.dataset.step)); });
  });

  document.addEventListener("keydown", function (e) {
    if (document.querySelector("dialog[open]")) return;
    if (e.key === "ArrowLeft") step(-1);
    if (e.key === "ArrowRight") step(1);
  });

  // Deslizamiento horizontal, como en la pantalla táctil.
  var startX = null;
  var stage = form.querySelector(".starters");
  stage.addEventListener("pointerdown", function (e) { startX = e.clientX; });
  stage.addEventListener("pointerup", function (e) {
    if (startX === null) return;
    var dx = e.clientX - startX;
    startX = null;
    if (Math.abs(dx) > 40) step(dx < 0 ? 1 : -1);
  });

  render();
})();
