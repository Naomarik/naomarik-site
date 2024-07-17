import htmx from 'htmx.org';


document.body.addEventListener('htmx:beforeSwap', function(evt) {
  let incomingDOM = new DOMParser().parseFromString(evt.detail.xhr.response, "text/html");
  // Transpose <meta> data, page-specific <link> tags and JSON-LD structured data
  // Note that hx-boost automatically swaps the <title> tag
  let selector = "head > meta:not([data-revision]), head *[rel='canonical']";
  document.querySelectorAll(selector).forEach((e) => {
    e.parentNode.removeChild(e);
  });
  incomingDOM.querySelectorAll(selector).forEach((e) => {
    if (e.tagName === 'SCRIPT') {
      document.body.appendChild(e);
    } else {
      document.head.appendChild(e);
    }
  })
});
