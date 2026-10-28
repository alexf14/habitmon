(function () {
  "use strict";

  // ---------- Scroll memory (lista) ----------

  var KEY = "habitmon.pokedex.scroll";
  var list = document.getElementById("dex-list");

  if (list) {
    try {
      var saved = sessionStorage.getItem(KEY);
      if (saved !== null) {
        list.scrollTop = Number(saved);
        sessionStorage.removeItem(KEY);
      }
    } catch (e) { /* almacenamiento no disponible */ }

    list.addEventListener("click", function (e) {
      if (!e.target.closest("a.dex-row")) return;
      try { sessionStorage.setItem(KEY, String(list.scrollTop)); } catch (err) { /* ignorar */ }
    });
  }

  // ---------- Cambio de compañero (ficha) ----------

  var setBtn = document.getElementById("set-companion-btn");
  if (!setBtn) return;

  var confirmDialog  = document.getElementById("companion-confirm-dialog");
  var confirmText    = document.getElementById("companion-confirm-text");
  var confirmYes     = document.getElementById("companion-confirm-yes");
  var confirmNo      = document.getElementById("companion-confirm-no");
  var successDialog  = document.getElementById("companion-success-dialog");
  var successText    = document.getElementById("companion-success-text");
  var successClose   = document.getElementById("companion-success-close");

  var pendingId   = null;
  var pendingName = null;
  var busy        = false;

  setBtn.addEventListener("click", function () {
    pendingId   = setBtn.dataset.pokemonId;
    pendingName = setBtn.dataset.name;
    confirmText.textContent = "\xBFQUIERES ESTABLECER A " + pendingName + " COMO TU COMPA\xD1ERO?";
    confirmDialog.showModal();
  });

  confirmNo.addEventListener("click", function () {
    confirmDialog.close();
    pendingId = pendingName = null;
  });

  confirmYes.addEventListener("click", function () {
    if (busy) return;
    busy = true;
    confirmYes.disabled = true;

    fetch(window.COMPANION_URL, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ pokemon_id: Number(pendingId) }),
    })
      .then(function (res) { return res.json(); })
      .then(function (data) {
        confirmDialog.close();
        if (data.code === 200) {
          successText.textContent = "\xA1" + pendingName + " ES AHORA TU COMPA\xD1ERO!";
          successDialog.showModal();
        } else {
          confirmText.textContent = "ERROR: " + (data.message || "Inténtalo de nuevo.");
          busy = false;
          confirmYes.disabled = false;
          confirmDialog.showModal();
        }
      })
      .catch(function () {
        confirmDialog.close();
        confirmText.textContent = "ERROR DE RED. INT\xC9NTALO DE NUEVO.";
        busy = false;
        confirmYes.disabled = false;
        confirmDialog.showModal();
      });
  });

  successClose.addEventListener("click", function () {
    successDialog.close();
    // Ocultar el botón y mostrar el indicador de compañero activo
    setBtn.parentElement.innerHTML =
      '<p class="dex-companion-current">' +
      '<span class="pokeball--mini" style="width:20px;height:20px;margin-left:0" aria-hidden="true"></span>' +
      ' TU COMPA\xD1ERO ACTUAL</p>';
  });
})();
