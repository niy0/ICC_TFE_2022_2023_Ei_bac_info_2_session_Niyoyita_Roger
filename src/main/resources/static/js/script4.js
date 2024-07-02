
function getListLigneDeCommandePanier(panierId) {
    var panierIdValue = parseInt(panierId);
    var totalPanier = 0.0;

    $.ajax({
        type: "GET",
        url: "/api/" + panierIdValue + "/lignesdecommande",
        dataType: "json",
        success: function (data) {
            $("#cart-items").empty();
            if (data.length === 0) {
                $("#cart-items").append('<tr><td colspan="4">Votre panier est vide7778888.</td></tr>');
            } else {
                $.each(data, function (index, ligneDeCommande) {
                    var montantTotal = ligneDeCommande.quantite * ligneDeCommande.prixUnitaire;
                    totalPanier += montantTotal;
                    var newRow = '<tr>' +
                        '<td>' + ligneDeCommande.nomProduit + '</td>' +
                        '<td>' + ligneDeCommande.prixUnitaire.toFixed(2) + ' €</td>' +
                        '<td>' + ligneDeCommande.quantite + '</td>' +
                        '<td>' + montantTotal.toFixed(2) + ' €</td>' +
                        '</tr>';
                    $("#cart-items").append(newRow);
                });
            }
            // Mettre à jour le total du panier
            $("#panier-total").text(totalPanier.toFixed(2));
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erreur de requête Ajax: " + textStatus + ", " + errorThrown);
            alert("Une erreur est survenue lors de la récupération des lignes de commande du panier.");
        }
    });
}

function addToCart(produitId) {
    // Vérifiez si produitId peut être converti en un entier
    var produitIdValue = parseInt(produitId);
    if (isNaN(produitIdValue)) {
        console.error('Erreur : produitId invalide');
        return;
    }

    // Obtenez la quantité depuis l'élément input dans la même div
    var quantiteInput = $("#quantite-" + produitIdValue);
    var quantite = parseInt(quantiteInput.val());
    if (quantite <= 0) {
        console.error('Quantité invalide');
        return;
    }
    var panierId = $("#panierId").val();
    var panierIdValue = parseInt(panierId);

    // Récupérez le jeton CSRF depuis les balises meta
    var csrfToken = $("meta[name='_csrf']").attr("content");

    // Créez un objet contenant les données à envoyer
    var data = {
        produitId: produitIdValue,
        panierId: panierIdValue,
        quantite: quantite
    };

    // Effectuez la requête Ajax POST
    $.ajax({
        url: '/addToCart',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        beforeSend: function(xhr) {
            // Ajoutez le jeton CSRF aux en-têtes de la requête
            xhr.setRequestHeader('X-XSRF-TOKEN', csrfToken);
        },
        success: function (response) {
            console.log('Ligne de commande ajoutée avec succès :', response);
            // Réalisez les actions nécessaires en cas de succès
            getListLigneDeCommandePanier(panierIdValue);

            // Rafraîchir la page uniquement pour le premier produit ajouté
            // Vérifiez si le panier était initialement vide et si c'est le cas, rechargez la page
            if ($("#cart-items").children().length === 0) {
                location.reload(); // Rafraîchit la page actuelle
            } else {
                getListLigneDeCommandePanier(panierIdValue);
            }
        },
        error: function (error) {
            console.log(produitIdValue, panierIdValue, quantite, data);
            var errorMessage = error.responseJSON ? error.responseJSON.message : error.responseText;
            console.error('Erreur lors de l\'ajout de la ligne de commande :', errorMessage);
            alert("Erreur lors de l'ajout du produit au panier. Veuillez réessayer.");
        }
    });

    // Réinitialiser la quantité à 1
    quantiteInput.val(1);
}

$(document).ready(function () {
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("name");

    $("#confirmerPanierBtn").click(function() {
        var ligneDeCommandeId = $(this).data("id"); // Récupérer l'ID stocké dans data-id

        $.ajax({
            url: "/delete/" + ligneDeCommandeId, // Construire l'URL avec l'ID
            type: "POST",
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken); // Ajouter le jeton CSRF à l'en-tête de la requête
            },
            success: function(response) {
                // Traitement en cas de succès (par exemple, afficher un message ou rediriger)
                alert("Ligne de commande supprimée avec succès.");
                window.location.href = "/panier"; // Rediriger vers le panier
            },
            error: function(xhr, status, error) {
                // Traitement en cas d'erreur
                alert("Erreur lors de la suppression : " + error);
            }
        });
    });

    // Fonction pour récupérer le panier en spécifiant l'ID du panier
    function getPanier(idPanier, callback) {
        var idPanierval = parseInt(idPanier);

        // Récupération du jeton CSRF depuis les balises meta
        var csrfParameterName = $("meta[name='_csrf.parameterName']").attr("content");
        var csrfToken = $("meta[name='_csrf.token']").attr("content");

        // en-tête avec le jeton CSRF
        var headers = {};
        headers[csrfParameterName] = csrfToken;

        $.ajax({
            url: '/panier/api/' + idPanierval, // Utilisez l'ID du panier spécifié
            type: 'GET',
            dataType: 'json',
            headers: headers, // Ajoutez le jeton CSRF dans les en-têtes de la requête
            success: function (panierData) {
                // La réponse a été reçue avec succès
                console.log('Panier récupéré avec succès :', panierData);
                if (callback) callback(panierData); // Exécutez la fonction de callback avec les données du panier
            },
            error: function (xhr, status, error) {
                console.error('Erreur AJAX :', error);
            }
        });
    }

    // Suppression d'une ligne de commande
    $(document).on('click', '.delete-line', function(event) {
         event.preventDefault();
         var form = $(this).closest('form');
         var ligneDeCommandeId = form.closest('tr').data('ligne-commande-id');

         $.ajax({
              url: '/lignedecommande/delete/' + ligneDeCommandeId,
              type: 'POST',
              beforeSend: function(xhr) {
                  xhr.setRequestHeader(csrfHeader, csrfToken);
              },
              success: function(response) {
                  // Met à jour l'affichage du panier
                  location.reload();
              },
              error: function(xhr, status, error) {
                  alert("Erreur lors de la suppression de la ligne de commande : " + error);
              }
         });
    });

    // Fonction pour mettre à jour une ligne de commande
    function updateLigneDeCommande(ligneDeCommandeId, ligneDeCommandePanierId, nouvelleQuantite) {
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/lignedecommande/update',
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
                console.log("Ligne de commande mise à jour avec succès:", response);
                // Assurez-vous que "nouveauTotalLigne" est un attribut retourné par votre API avec le nouveau total pour cette ligne
                $("#total-ligne-" + ligneDeCommandeId).text(response.nouveauTotalLigne + ' €');
                // Mettre à jour le total du panier après avoir mis à jour une ligne de commande
                updateCartTotal(ligneDeCommandePanierId); // rajouter l'id du panier !!!
            },
            error: function(xhr, status, error) {
                console.error("Erreur lors de la mise à jour de la ligne de commande:", error);
            }
        });
    }

    // Fonction pour mettre à jour le total du panier
    function updateCartTotal(idPanier) {
        var idPanierval = parseInt(idPanier);
        getPanier(idPanierval, function(panier) {
            console.log(panier);
            var total = panier.montantTotalPanier;
            $('#panier-total').text(total.toFixed(2)); // N'oubliez pas d'ajouter ' €' pour l'affichage de la devise
        });
    }

    // Fonction pour ajouter un produit au panier
    function addToCart(produitId, quantite) {
        var csrfToken = $("meta[name='_csrf']").attr("content");

        $.ajax({
            url: '/addToCart',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ produitId: produitId, quantite: quantite }),
            beforeSend: function(xhr) {
                xhr.setRequestHeader('X-CSRF-TOKEN', csrfToken);
            },
            success: function(response) {
                console.log('Produit ajouté avec succès:', response);
                // Ajoutez ici la logique pour mettre à jour l'affichage du panier ou rediriger l'utilisateur
            },
            error: function(error) {
                console.error('Erreur lors de l\'ajout au panier:', error);
            }
        });
    }

    $('#cart-items').on('change', '.quantity-input', function() {
        var ligneDeCommandeId = $(this).closest('tr').data('ligne-commande-id'); // Récupère l'ID de la ligne de commande
        var ligneDeCommandePanierId = $(this).closest('tr').find('[name="ligneDeCommandePanierId"]').text();
        var quantity = parseInt($(this).val());
        var price = parseFloat($(this).closest('tr').find('#prix-produit').text());
        var total = quantity * price;

        // Met à jour l'affichage du total pour cette ligne de commande dans le tableau
        $(this).closest('tr').find('#total-produit').text(total.toFixed(2));

        // Appel AJAX pour mettre à jour la quantité et potentiellement le total côté serveur
        updateLigneDeCommande(ligneDeCommandeId, ligneDeCommandePanierId, quantity);
    });

    // Fonction pour initialiser les quantités max des produits basées sur le stock
    function initialiserQuantitesMax() {
        $('.quantity-input').each(function() {
            var produitId = $(this).data('product-id');
            getQuantiteProduit(produitId, $(this));
        });
    }

    // Fonction pour obtenir la quantité maximale d'un produit
    function getQuantiteProduit(produitId, inputElement) {
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var headers = { "X-CSRF-TOKEN": csrfToken };

        $.ajax({
            type: "GET",
            url: "/quantite-produit/" + produitId,
            headers: headers,
            success: function(quantite) {
                inputElement.attr('max', quantite);
            },
            error: function(error) {
                console.error('Erreur lors de la récupération de la quantité du produit:', error);
            }
        });
    }

    initialiserQuantitesMax();

    // Autres fonctions nécessaires (si vous en avez ajouté d'autres qui doivent être incluses)

    // Fonction pour obtenir les détails du produit, y compris la quantité mise à jour
    function getProduitDetails(produitId) {
        // Convertir produitId en un Long
        var produitIdValue = parseInt(produitId);

        // Récupérer le jeton CSRF depuis les balises meta
        var csrfParameterName = $("meta[name='_csrf.parameterName']").attr("content");
        var csrfToken = $("meta[name='_csrf.token']").attr("content");

        // Créer un objet d'en-tête avec le jeton CSRF
        var headers = {};
        headers[csrfParameterName] = csrfToken;

        $.ajax({
            type: "GET",
            url: "/produit/api/" + produitIdValue,
            dataType: "json",
            headers: headers, // Ajoutez le jeton CSRF dans les en-têtes de la requête
            success: function (data) {
                var nouvelleQuantite = data.quantite; // Assurez-vous que la propriété "quantite" correspond à la nouvelle quantité

                // Mettez à jour la valeur maximale (max) de l'élément input avec la nouvelle quantité
                $("#quantite-" + produitId).attr('max', nouvelleQuantite);
            },
            error: function (error) {
                console.error('Erreur lors de la récupération des détails du produit :', error);
                // Afficher un message d'erreur à l'utilisateur
                alert("Erreur lors de l'ajout du produit au panier. Veuillez réessayer.");
            }
        });
    }
});