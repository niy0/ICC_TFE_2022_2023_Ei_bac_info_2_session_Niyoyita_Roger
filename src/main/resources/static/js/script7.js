$(document).ready(function () {
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    // Ajouter un écouteur d'événements au bouton de paiement Stripe
    $('#checkout-button').on('click', function () {
        console.log("Bouton Confirmer Panier cliqué.");

        const stripe = Stripe('pk_test_51LUs5LDjQcavZkZrmoldBAz0GwZXQK4EYEHJDMs3NQiqYZAMdaKG8z1MF8kJmgOO1sKNFW2ZqG30JtRTG9BLoHU300IugCOShr');

        // Définir items ici
        var items = [];
        $('#cart-items tr').each(function () {
            var item = {
                name: $(this).find('td[name="nom_produit"]').text(),
                price: parseFloat($(this).find('#prix-produit').text()) * 100, // Convertir en cents
                quantity: parseInt($(this).find('.quantity-input').val())
            };
            items.push(item);
        });

        console.log("Articles du panier:");
        console.table(items); // Pour vérifier la structure des articles

        fetch('/api/checkout/create-session', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({ items: items })
        })
        .then(function (response) {
            if (!response.ok) {
                return response.json().then(err => {throw new Error('Network response was not ok: ' + err.message)});
            }
            return response.json();
        })
        .then(function (data) {
            if (data.id) {
                return stripe.redirectToCheckout({ sessionId: data.id });
            } else {
                throw new Error('Session ID non trouvé dans la réponse.');
            }
        })
        .then(function (result) {
            if (result.error) {
                alert(result.error.message);
            }
        })
        .catch(function (error) {
            console.error('Error:', error);
            alert('Une erreur est survenue lors de la création de la session de paiement. Veuillez réessayer.');
        });
    });


    // Suppression du premier élément
    $(document).on('click', '.delete-first-line', function(event) {
        event.preventDefault();
        var form = $(this).closest('form');
        var panierId = form.find('input[name="idPanier"]').val();
        var ligneDeCommandeId = form.find('input[name="idLigneDeCommande"]').val();

        $.ajax({
            url: '/deleteFirstElemPanier',
            type: 'POST',
            data: {
                idPanier: panierId,
                idLigneDeCommande: ligneDeCommandeId
            },
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function(response) {
                location.reload();
            },
            error: function(xhr, status, error) {
                alert("Erreur lors de la suppression de la ligne de commande : " + error);
            }
        });
    });

    // Fonction pour récupérer les lignes de commande du panier
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
                    $("#cart-items").append('<tr><td colspan="4">Votre panier est vide.</td></tr>');
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
                $("#panier-total").text(totalPanier.toFixed(2) + ' €');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Erreur de requête Ajax: " + textStatus + ", " + errorThrown);
                alert("Une erreur est survenue lors de la récupération des lignes de commande du panier.");
            }
        });
    }

    // Fonction pour ajouter un produit au panier
    function addToCart(produitId) {
        var produitIdValue = parseInt(produitId);
        if (isNaN(produitIdValue)) {
            console.error('Erreur : produitId invalide');
            return;
        }

        var quantiteInput = $("#quantite-" + produitIdValue);
        var quantite = parseInt(quantiteInput.val());
        if (quantite <= 0) {
            console.error('Quantité invalide');
            return;
        }

        var panierId = $("#panierId").val();
        var panierIdValue = parseInt(panierId);

        var data = {
            produitId: produitIdValue,
            panierId: panierIdValue,
            quantite: quantite
        };

        $.ajax({
            url: '/addToCart',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (response) {
                console.log('Ligne de commande ajoutée avec succès :', response);
                getListLigneDeCommandePanier(panierIdValue);

                if ($("#cart-items").children().length === 0) {
                    location.reload();
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

        quantiteInput.val(1);
    }

    // Fonction pour récupérer le panier en spécifiant l'ID du panier
    function getPanier(idPanier, callback) {
        var idPanierval = parseInt(idPanier);

        var csrfParameterName = $("meta[name='_csrf.parameterName']").attr("content");
        var csrfToken = $("meta[name='_csrf.token']").attr("content");

        var headers = {};
        headers[csrfParameterName] = csrfToken;

        $.ajax({
            url: '/panier/api/' + idPanierval,
            type: 'GET',
            dataType: 'json',
            headers: headers,
            success: function (panierData) {
                console.log('Panier récupéré avec succès :', panierData);
                if (callback) callback(panierData);
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
                location.reload();
            },
            error: function(xhr, status, error) {
                alert("Erreur lors de la suppression de la ligne de commande : " + error);
            }
        });
    });

    // Fonction pour mettre à jour une ligne de commande
    function updateLigneDeCommande(ligneDeCommandeId, ligneDeCommandePanierId, nouvelleQuantite) {
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
                $("#total-ligne-" + ligneDeCommandeId).text(response.nouveauTotalLigne + ' €');
                updateCartTotal(ligneDeCommandePanierId);
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
            var total = panier.montantTotalPanier;
            $('#panier-total').text(total.toFixed(2) ); //icicicicicicic
        });
    }

    $('#cart-items').on('change', '.quantity-input', function() {
        var ligneDeCommandeId = $(this).closest('tr').data('ligne-commande-id');
        var ligneDeCommandePanierId = $(this).closest('tr').find('[name="ligneDeCommandePanierId"]').text();
        var quantity = parseInt($(this).val());
        var price = parseFloat($(this).closest('tr').find('#prix-produit').text());
        var total = quantity * price;

        $(this).closest('tr').find('#total-produit').text(total.toFixed(2));

        updateLigneDeCommande(ligneDeCommandeId, ligneDeCommandePanierId, quantity);
        updateLocalStorage();
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
        $.ajax({
            type: "GET",
            url: "/quantite-produit/" + produitId,
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function(quantite) {
                inputElement.attr('max', quantite);
            },
            error: function(error) {
                console.error('Erreur lors de la récupération de la quantité du produit:', error);
            }
        });
    }

    initialiserQuantitesMax();

    // Fonction pour obtenir les détails du produit, y compris la quantité mise à jour
    function getProduitDetails(produitId) {
        var produitIdValue = parseInt(produitId);

        var csrfParameterName = $("meta[name='_csrf.parameterName']").attr("content");
        var csrfToken = $("meta[name='_csrf.token']").attr("content");

        var headers = {};
        headers[csrfParameterName] = csrfToken;

        $.ajax({
            type: "GET",
            url: "/produit/api/" + produitIdValue,
            dataType: "json",
            headers: headers,
            success: function (data) {
                var nouvelleQuantite = data.quantite;

                $("#quantite-" + produitId).attr('max', nouvelleQuantite);
            },
            error: function (error) {
                console.error('Erreur lors de la récupération des détails du produit :', error);
                alert("Erreur lors de l'ajout du produit au panier. Veuillez réessayer.");
            }
        });
    }
});
