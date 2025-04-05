// Toggle alert panels
function toggleAlert(element) {
  const alertBody = element.nextElementSibling;
  const chevron = element.querySelector('.alert-toggle i');

  if (alertBody.style.display === 'none' || !alertBody.style.display) {
    alertBody.style.display = 'block';
    chevron.classList.replace('fa-chevron-down', 'fa-chevron-up');
  } else {
    alertBody.style.display = 'none';
    chevron.classList.replace('fa-chevron-up', 'fa-chevron-down');
  }
}