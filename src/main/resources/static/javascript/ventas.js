function gestionarCarrito(url, paramName, valor) {
    const formData = new FormData();
    formData.append(paramName, valor);

    fetch(url, {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error en la respuesta del servidor');
        }
        return response.text();
    })
    .then(html => {
        
        const container = document.getElementById('cart-panel-content');
        if (container) {
            container.innerHTML = html;
        }
    })
    .catch(error => {
        console.error('Error al gestionar el carrito:', error);
    });
}