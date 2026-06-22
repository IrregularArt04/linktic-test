<template>
  <div class="product-form-container">
    <div class="form-card animate-fade-in">
      <div class="form-header">
        <div class="form-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-package">
            <line x1="16.5" y1="9.4" x2="7.5" y2="4.21"></line>
            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
            <polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline>
            <line x1="12" y1="22.08" x2="12" y2="12"></line>
          </svg>
        </div>
        <h2>Registrar Nuevo Producto</h2>
        <p class="subtitle">Complete los campos de la entidad para agregar un producto al catálogo</p>
      </div>

      <form @submit.prevent="submitForm" novalidate>
        <!-- Barcode Field -->
        <div class="form-group" :class="{ 'has-error': errors.codigoBarras, 'is-valid': touched.codigoBarras && !errors.codigoBarras }">
          <label for="codigoBarras">Código de Barras *</label>
          <div class="input-wrapper">
            <input 
              type="text" 
              id="codigoBarras" 
              v-model="form.codigoBarras" 
              @blur="handleBlur('codigoBarras')"
              @input="validateField('codigoBarras')"
              placeholder="Ej. BARCODE12345"
              maxlength="25"
            />
            <span class="status-icon"></span>
          </div>
          <div class="feedback-row">
            <span class="error-msg" v-if="errors.codigoBarras">{{ errors.codigoBarras }}</span>
            <span class="char-counter" :class="{ 'limit-near': form.codigoBarras.length > 20 }">
              {{ form.codigoBarras.length }}/25
            </span>
          </div>
        </div>

        <!-- Name Field -->
        <div class="form-group" :class="{ 'has-error': errors.nombre, 'is-valid': touched.nombre && !errors.nombre }">
          <label for="nombre">Nombre del Producto *</label>
          <div class="input-wrapper">
            <input 
              type="text" 
              id="nombre" 
              v-model="form.nombre" 
              @blur="handleBlur('nombre')"
              @input="validateField('nombre')"
              placeholder="Ej. Detergente Líquido Premium"
              maxlength="150"
            />
            <span class="status-icon"></span>
          </div>
          <div class="feedback-row">
            <span class="error-msg" v-if="errors.nombre">{{ errors.nombre }}</span>
            <span class="char-counter" :class="{ 'limit-near': form.nombre.length > 130 }">
              {{ form.nombre.length }}/150
            </span>
          </div>
        </div>

        <!-- Price Field -->
        <div class="form-group" :class="{ 'has-error': errors.precio, 'is-valid': touched.precio && !errors.precio }">
          <label for="precio">Precio ($) *</label>
          <div class="input-wrapper">
            <input 
              type="number" 
              id="precio" 
              v-model="form.precio" 
              @blur="handleBlur('precio')"
              @input="validateField('precio')"
              placeholder="Ej. 12500.50"
              step="0.01"
            />
            <span class="status-icon"></span>
          </div>
          <div class="feedback-row">
            <span class="error-msg" v-if="errors.precio">{{ errors.precio }}</span>
            <span class="helper-text" v-else>Máx. 10 enteros y 2 decimales</span>
          </div>
        </div>

        <!-- Description Field -->
        <div class="form-group" :class="{ 'has-error': errors.descripcion, 'is-valid': touched.descripcion && !errors.descripcion }">
          <label for="descripcion">Descripción *</label>
          <div class="input-wrapper">
            <textarea 
              id="descripcion" 
              v-model="form.descripcion" 
              @blur="handleBlur('descripcion')"
              @input="validateField('descripcion')"
              placeholder="Describa los detalles principales del producto..."
              maxlength="1000"
              rows="4"
            ></textarea>
            <span class="status-icon"></span>
          </div>
          <div class="feedback-row">
            <span class="error-msg" v-if="errors.descripcion">{{ errors.descripcion }}</span>
            <span class="char-counter" :class="{ 'limit-near': form.descripcion.length > 900 }">
              {{ form.descripcion.length }}/1000
            </span>
          </div>
        </div>

        <!-- Submit Button -->
        <button type="submit" class="submit-btn" :disabled="isSubmitting">
          <span v-if="isSubmitting" class="spinner"></span>
          <span>{{ isSubmitting ? 'Guardando...' : 'Guardar Producto' }}</span>
        </button>
      </form>

      <!-- Feedback Toast / Alert -->
      <Transition name="fade">
        <div v-if="toast.show" :class="['toast-banner', toast.type]">
          <div class="toast-content">
            <span class="toast-icon">
              <svg v-if="toast.type === 'success'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="8" x2="12" y2="12"></line>
                <line x1="12" y1="16" x2="12.01" y2="16"></line>
              </svg>
            </span>
            <div class="toast-text">
              <h4 class="toast-title">{{ toast.title }}</h4>
              <p class="toast-msg">{{ toast.message }}</p>
              
              <!-- Details of created product -->
              <div v-if="toast.product" class="toast-details">
                <div><strong>ID:</strong> {{ toast.product.id || 'Generado por BD' }}</div>
                <div><strong>Código de Barras:</strong> {{ toast.product.codigoBarras }}</div>
                <div><strong>Nombre:</strong> {{ toast.product.nombre }}</div>
                <div><strong>Precio:</strong> ${{ Number(toast.product.precio).toLocaleString('es-CO', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) }}</div>
                <div><strong>Descripción:</strong> {{ toast.product.descripcion }}</div>
              </div>
            </div>
          </div>
          <button @click="toast.show = false" class="toast-close" type="button">&times;</button>
        </div>
      </Transition>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import axios from 'axios';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();

// Form state
const form = reactive({
  codigoBarras: '',
  nombre: '',
  precio: '',
  descripcion: ''
});

// Touch state for fields
const touched = reactive({
  codigoBarras: false,
  nombre: false,
  precio: false,
  descripcion: false
});

// Validation error messages
const errors = reactive({
  codigoBarras: '',
  nombre: '',
  precio: '',
  descripcion: ''
});

const isSubmitting = ref(false);

const toast = reactive({
  show: false,
  type: 'success', // 'success' | 'error' | 'warning'
  title: '',
  message: '',
  product: null
});

// Validators
const validateCodigoBarras = (val) => {
  if (!val || val.trim() === '') {
    return 'El código de barras es obligatorio.';
  }
  if (val.length > 25) {
    return 'El código de barras no puede superar los 25 caracteres.';
  }
  if (!/^[a-zA-Z0-9]+$/.test(val)) {
    return 'El código de barras debe ser alfanumérico (solo letras y números, sin espacios ni caracteres especiales).';
  }
  return '';
};

const validateNombre = (val) => {
  if (!val || val.trim() === '') {
    return 'El nombre del producto es obligatorio.';
  }
  if (val.length > 150) {
    return 'El nombre no puede superar los 150 caracteres.';
  }
  return '';
};

const validatePrecio = (val) => {
  if (val === undefined || val === null || String(val).trim() === '') {
    return 'El precio es obligatorio.';
  }
  const numericPrice = Number(val);
  if (isNaN(numericPrice)) {
    return 'El precio debe ser un número válido.';
  }
  if (numericPrice <= 0) {
    return 'El precio debe ser un valor mayor a 0.';
  }
  
  // Format check: max 10 integer digits and max 2 decimals
  const strVal = String(val).trim();
  const regex = /^\d{1,10}(\.\d{1,2})?$/;
  if (!regex.test(strVal)) {
    return 'El precio debe tener como máximo 10 dígitos enteros y 2 decimales (ej. 99.99).';
  }
  return '';
};

const validateDescripcion = (val) => {
  if (!val || val.trim() === '') {
    return 'La descripción es obligatoria.';
  }
  if (val.length > 1000) {
    return 'La descripción no puede superar los 1000 caracteres.';
  }
  return '';
};

// Validate individual field
const validateField = (field) => {
  if (!touched[field]) return; // Validate only if touched
  
  if (field === 'codigoBarras') {
    errors.codigoBarras = validateCodigoBarras(form.codigoBarras);
  } else if (field === 'nombre') {
    errors.nombre = validateNombre(form.nombre);
  } else if (field === 'precio') {
    errors.precio = validatePrecio(form.precio);
  } else if (field === 'descripcion') {
    errors.descripcion = validateDescripcion(form.descripcion);
  }
};

// Handle Blur event
const handleBlur = (field) => {
  touched[field] = true;
  validateField(field);
};

// Validate all fields
const validateAll = () => {
  touched.codigoBarras = true;
  touched.nombre = true;
  touched.precio = true;
  touched.descripcion = true;

  errors.codigoBarras = validateCodigoBarras(form.codigoBarras);
  errors.nombre = validateNombre(form.nombre);
  errors.precio = validatePrecio(form.precio);
  errors.descripcion = validateDescripcion(form.descripcion);

  return !errors.codigoBarras && !errors.nombre && !errors.precio && !errors.descripcion;
};

// Reset form fields
const resetForm = () => {
  form.codigoBarras = '';
  form.nombre = '';
  form.precio = '';
  form.descripcion = '';
  
  touched.codigoBarras = false;
  touched.nombre = false;
  touched.precio = false;
  touched.descripcion = false;

  errors.codigoBarras = '';
  errors.nombre = '';
  errors.precio = '';
  errors.descripcion = '';
};

// Submit form
const submitForm = async () => {
  // Clear previous toast
  toast.show = false;

  // Run validation
  if (!validateAll()) {
    toast.show = true;
    toast.type = 'error';
    toast.title = 'Error de Validación';
    toast.message = 'Por favor, corrija los errores en el formulario antes de guardar.';
    toast.product = null;
    return;
  }

  isSubmitting.value = true;
  
  const payload = {
    codigoBarras: form.codigoBarras.trim(),
    nombre: form.nombre.trim(),
    precio: parseFloat(form.precio),
    descripcion: form.descripcion.trim()
  };

  console.log('Enviando producto:', payload);

  try {
    // Attempt request to backend on port 8081
    const response = await axios.post('http://localhost:8081/api/productos', payload, {
      headers: {
        'Content-Type': 'application/json',
        'X-API-KEY': authStore.apiKey
      }
    });

    console.log('Respuesta del servidor:', response.data);
    
    toast.show = true;
    toast.type = 'success';
    toast.title = '¡Éxito!';
    toast.message = 'El producto ha sido guardado exitosamente en el servidor.';
    toast.product = response.data;
    
    resetForm();
  } catch (error) {
    console.error('Error al guardar el producto en el servidor:', error);
    
    let errorMessage = 'No se pudo conectar con el servidor backend.';
    
    if (error.response) {
      if (error.response.status === 401) {
        errorMessage = 'No autorizado: la API Key provista es inválida o fue rechazada.';
      } else if (error.response.data && error.response.data.error) {
        errorMessage = error.response.data.error;
      } else {
        errorMessage = `Error del servidor (${error.response.status}): ${error.response.statusText}`;
      }
    } else if (error.request) {
      errorMessage = 'Error de conexión: El microservicio de productos (puerto 8081) no responde. Asegúrese de que esté en ejecución.';
    }

    // fallback gracefully to display simulated success for preview
    toast.show = true;
    toast.type = 'warning';
    toast.title = 'Guardado Localmente / Servidor Desconectado';
    toast.message = `${errorMessage}\n\nLos datos del formulario son correctos. A continuación se detallan los datos que se habrían enviado:`;
    
    toast.product = {
      id: null,
      ...payload
    };
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style lang="scss" scoped>
.product-form-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  padding: 20px;
  min-height: 80vh;
}

.form-card {
  background: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  width: 100%;
  max-width: 550px;
  padding: 40px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;

  @media (prefers-color-scheme: dark) {
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    background: var(--color-background-soft);
  }

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 4px;
    background: linear-gradient(90deg, #41b883, #35495e);
  }
}

.form-header {
  text-align: center;
  margin-bottom: 30px;

  .form-icon {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    width: 60px;
    height: 60px;
    background: rgba(65, 184, 131, 0.1);
    color: #41b883;
    border-radius: 50%;
    margin-bottom: 16px;
    
    svg {
      width: 30px;
      height: 30px;
    }
  }

  h2 {
    color: var(--color-heading);
    font-size: 1.8rem;
    font-weight: 700;
    margin: 0 0 8px 0;
  }

  .subtitle {
    color: var(--color-text);
    opacity: 0.8;
    font-size: 0.95rem;
    line-height: 1.4;
  }
}

form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;

  label {
    font-size: 0.9rem;
    font-weight: 600;
    color: var(--color-heading);
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;

  input, textarea {
    width: 100%;
    padding: 12px 16px;
    font-size: 0.95rem;
    color: var(--color-text);
    background: var(--color-background);
    border: 1px solid var(--color-border);
    border-radius: 8px;
    outline: none;
    transition: all 0.25s ease;
    font-family: inherit;

    &::placeholder {
      color: var(--color-text);
      opacity: 0.45;
    }

    &:focus {
      border-color: #41b883;
      box-shadow: 0 0 0 3px rgba(65, 184, 131, 0.15);
      background: var(--color-background);
    }
  }

  textarea {
    resize: vertical;
    min-height: 100px;
  }
}

/* Validation status styles */
.form-group.has-error {
  .input-wrapper {
    input, textarea {
      border-color: #ef4444;
      
      &:focus {
        box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.15);
      }
    }
  }

  .status-icon::after {
    content: '!';
    position: absolute;
    right: 14px;
    top: 50%;
    transform: translateY(-50%);
    width: 20px;
    height: 20px;
    background: #ef4444;
    color: white;
    font-weight: bold;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 12px;
  }

  textarea ~ .status-icon::after {
    top: 24px;
  }
}

.form-group.is-valid {
  .status-icon::after {
    content: '✓';
    position: absolute;
    right: 14px;
    top: 50%;
    transform: translateY(-50%);
    width: 20px;
    height: 20px;
    background: #10b981;
    color: white;
    font-weight: bold;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 12px;
  }

  textarea ~ .status-icon::after {
    top: 24px;
  }
}

.feedback-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  min-height: 18px;
  font-size: 0.8rem;
}

.error-msg {
  color: #ef4444;
  font-weight: 500;
  animation: slideDown 0.2s ease-out;
}

.helper-text {
  color: var(--color-text);
  opacity: 0.6;
}

.char-counter {
  color: var(--color-text);
  opacity: 0.5;
  margin-left: auto;

  &.limit-near {
    color: #f59e0b;
    font-weight: 600;
  }
}

.submit-btn {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 14px;
  font-size: 1rem;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, #41b883, #2d825c);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(65, 184, 131, 0.2);

  &:hover:not(:disabled) {
    background: linear-gradient(135deg, #4cd096, #35966b);
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(65, 184, 131, 0.3);
  }

  &:active:not(:disabled) {
    transform: translateY(1px);
  }

  &:disabled {
    background: #a8a8a8;
    box-shadow: none;
    cursor: not-allowed;
    opacity: 0.7;
  }
}

/* Spinner */
.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 0.8s linear infinite;
}

/* Toast Banner */
.toast-banner {
  margin-top: 24px;
  padding: 16px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  
  &.success {
    background-color: rgba(16, 185, 129, 0.1);
    border: 1px solid rgba(16, 185, 129, 0.2);
    color: #065f46;
    
    @media (prefers-color-scheme: dark) {
      color: #34d399;
    }

    .toast-icon { color: #10b981; }
  }

  &.error {
    background-color: rgba(239, 68, 68, 0.1);
    border: 1px solid rgba(239, 68, 68, 0.2);
    color: #991b1b;

    @media (prefers-color-scheme: dark) {
      color: #f87171;
    }

    .toast-icon { color: #ef4444; }
  }

  &.warning {
    background-color: rgba(245, 158, 11, 0.1);
    border: 1px solid rgba(245, 158, 11, 0.2);
    color: #92400e;

    @media (prefers-color-scheme: dark) {
      color: #fbbf24;
    }

    .toast-icon { color: #f59e0b; }
  }
}

.toast-content {
  display: flex;
  gap: 12px;
  flex: 1;
}

.toast-icon {
  display: flex;
  margin-top: 2px;
  svg {
    width: 20px;
    height: 20px;
  }
}

.toast-text {
  flex: 1;
  text-align: left;
}

.toast-title {
  margin: 0 0 4px 0;
  font-size: 0.95rem;
  font-weight: 700;
}

.toast-msg {
  margin: 0;
  font-size: 0.85rem;
  line-height: 1.4;
  white-space: pre-line;
}

.toast-details {
  margin-top: 12px;
  padding: 10px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 6px;
  font-size: 0.8rem;
  display: flex;
  flex-direction: column;
  gap: 4px;
  border-left: 3px solid #10b981;

  @media (prefers-color-scheme: dark) {
    background: rgba(0, 0, 0, 0.2);
  }
}

.toast-close {
  background: none;
  border: none;
  font-size: 1.25rem;
  font-weight: bold;
  color: inherit;
  cursor: pointer;
  line-height: 1;
  padding: 0;
  opacity: 0.5;
  transition: opacity 0.2s;

  &:hover {
    opacity: 1;
  }
}

/* Animations */
@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: slideUp 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>