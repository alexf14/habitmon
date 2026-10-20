(function () {
  "use strict";

  var form = document.getElementById("intro-form");
  var lines = Array.prototype.slice.call(form.querySelectorAll(".intro-line"));
  var texts = lines.map(function (l) { return l.textContent; });
  var index = -1;
  var timer = null;

  // Escribe la línea letra a letra, como en los juegos clásicos.
  function show(i) {
    index = i;
    lines.forEach(function (l, j) { l.classList.toggle("is-current", j === i); });
    var line = lines[i];
    var chars = 0;
    line.textContent = "";
    form.classList.add("is-typing");
    timer = setInterval(function () {
      chars++;
      line.textContent = texts[i].slice(0, chars);
      if (chars >= texts[i].length) finish();
    }, 35);
  }

  function finish() {
    clearInterval(timer);
    timer = null;
    lines[index].textContent = texts[index];
    form.classList.remove("is-typing");
  }

  form.addEventListener("submit", function (e) {
    if (timer) { e.preventDefault(); finish(); return; }
    if (index < lines.length - 1) { e.preventDefault(); show(index + 1); }
    // En la última línea el formulario se envía y se marca la presentación como completada.
  });

  form.classList.add("is-scripted");
  setTimeout(function () { show(0); }, 1300);
})();
