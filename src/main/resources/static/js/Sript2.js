$(document).ready(function() {
    // Fonctions de gestion de la quantité et du panier
    handleQuantityChange();
    handleModalDismiss();

    // Fonctions AJAX pour la gestion des produits et du panier
    loadActiveProducts();
});

function handleQuantityChange() {
    $('#cart-items').on('change', '.quantity-input', function() {
        var ligneDeCommandeId = $(this).closest('tr').data('ligne-commande-id');
        var quantity = parseInt($(this).val());
        var price = parseFloat($(this).closest('tr').find('.prix-produit').text());
        var total = quantity * price;

        // Met à jour l'affichage du total pour cette ligne de commande
        $(this).closest('tr').find('.total-produit').text(total.toFixed(2) + ' €');

        // Mise à jour côté serveur
        updateLigneDeCommande(ligneDeCommandeId, quantity);

        // Mise à jour du total du panier
        updateCartTotal();
    });
}

function updateCartTotal() {
    var total = 0;
    $('.product-total').each(function() {
        total += parseFloat($(this).text());
    });
    $('#panier-total').text(total.toFixed(2) + ' €');
}

function updateLigneDeCommande(ligneDeCommandeId, nouvelleQuantite) {
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        url: '/api/ligne-de-commande/update',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            id: ligneDeCommandeId,
            quantite: nouvelleQuantite
        }),
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function(response) {
            console.log("Mise à jour réussie: ", response);
        },
        error: function(xhr, status, error) {
            console.error("Erreur mise à jour: ", error);
        }
    });
}

function loadActiveProducts() {
    // Cette fonction chargera les produits actifs et gérera l'affichage comme dans getProduitActif()
}

function handleModalDismiss() {
    // Cette fonction gèrera la fermeture du modal et les actions associées
}

// Les fonctions getQuantiteProduit et getProduitDetails2 peuvent être intégrées où elles sont nécessaires ou conservées séparément si elles sont appelées à plusieurs endroits.
