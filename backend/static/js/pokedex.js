(function () {
  "use strict";

  // Al abrir una ficha se guarda la posición de la lista; al volver se recupera una sola vez.
  var KEY = "habitmon.pokedex.scroll";
  var list = document.getElementById("dex-list");

  try {
    var saved = sessionStorage.getItem(KEY);
    if (saved !== null) {
      list.scrollTop = Number(saved);
      sessionStorage.removeItem(KEY);
    }
  } catch (e) { /* almacenamiento no disponible: se empieza arriba */ }

  list.addEventListener("click", function (e) {
    if (!e.target.closest("a.dex-row")) return;
    try { sessionStorage.setItem(KEY, String(list.scrollTop)); } catch (err) { /* ignorar */ }
  });
})();
