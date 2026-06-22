<template>
  <Transition name="fade">
    <div v-if="show" class="modal-backdrop" @click="closeModal">
      <div class="modal-card animate-scale-in" @click.stop>
        <!-- Header -->
        <div class="modal-header">
          <div class="header-title-container">
            <div class="header-icon">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
                <polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline>
                <line x1="12" y1="22.08" x2="12" y2="12"></line>
              </svg>
            </div>
            <div>
              <h3>Gestión de Inventario</h3>
              <p class="subtitle" v-if="product">{{ product.nombre }} ({{ product.codigoBarras }})</p>
              <p class="subtitle" v-else>Cargando información del producto...</p>
            </div>
          </div>
          <button class="close-btn" @click="closeModal" aria-label="Cerrar modal">&times;</button>
        </div>

        <!-- Body -->
        <div class="modal-body">
          <!-- Loading State -->
          <div v-if="isLoading" class="loading-state">
            <span class="spinner large"></span>
            <p>Obteniendo datos del servidor...</p>
          </div>

          <div v-else>
            <!-- Product Info Summary -->
            <div v-if="product" class="product-info-summary">
              <div class="info-item">
                <span class="label">ID Producto:</span>
                <span class="value">{{ product.id }}</span>
              </div>
              <div class="info-item">
                <span class="label">Precio:</span>
                <span class="value highlight">${{ Number(product.precio).toLocaleString('es-CO', { minimumFractionDigits: 2 }) }}</span>
              </div>
              <div class="info-item full">
                <span class="label">Descripción:</span>
                <span class="value desc">{{ product.descripcion }}</span>
              </div>
            </div>

            <!-- Inventory Status Card -->
            <div class="status-card" :class="stockStatusClass">
              <div class="status-main">
                <div class="status-icon-wrapper">
                  <svg v-if="inventory" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="6" y1="12" x2="18" y2="12"></line>
                    <polyline points="12 6 12 12 12 18"></polyline>
                  </svg>
                  <svg v-else xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="8" x2="12" y2="12"></line>
                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                  </svg>
                </div>
                <div class="status-info">
                  <span class="stock-title">Stock Actual en Almacén</span>
                  <h2 class="stock-count">
                    {{ inventory ? inventory.cantidad : 0 }} <span class="unit">unidades</span>
                  </h2>
                  <p class="last-update" v-if="inventory && inventory.actualizadoEn">
                    Última actualización: {{ formatDateTime(inventory.actualizadoEn) }}
                  </p>
                  <p class="last-update uninitialized" v-else>
                    Sin inventario inicial registrado
                  </p>
                </div>
              </div>
            </div>

            <!-- Action Tabs / Grid -->
            <div class="actions-grid">
              <!-- Update Stock (PUT) -->
              <div class="action-card">
                <h4>Actualizar Stock Total</h4>
                <p class="action-desc">Fija la cantidad de inventario al valor que especifiques en este campo.</p>
                <form @submit.prevent="updateStock" class="action-form">
                  <div class="input-group">
                    <input 
                      type="number" 
                      v-model.number="updateQty" 
                      placeholder="Nueva cantidad" 
                      min="0" 
                      required
                    />
                    <button type="submit" class="action-btn" :disabled="isUpdating">
                      <span v-if="isUpdating" class="spinner"></span>
                      <span>{{ isUpdating ? 'Guardando...' : 'Fijar Cantidad' }}</span>
                    </button>
                  </div>
                </form>
              </div>

              <!-- Simulate Sale / Process Purchase (POST) -->
              <div class="action-card">
                <h4>Registrar Venta / Compra</h4>
                <p class="action-desc">Descuenta unidades realizando un flujo de compra validado por el backend.</p>
                <form @submit.prevent="processPurchase" class="action-form">
                  <div class="input-group">
                    <input 
                      type="number" 
                      v-model.number="purchaseQty" 
                      placeholder="Cant. a vender/comprar" 
                      min="1" 
                      :max="inventory ? inventory.cantidad : 99999"
                      required
                    />
                    <button type="submit" class="action-btn purchase" :disabled="isPurchasing || (inventory && inventory.cantidad === 0)">
                      <span v-if="isPurchasing" class="spinner"></span>
                      <span>{{ isPurchasing ? 'Procesando...' : 'Comprar / Vender' }}</span>
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>

          <!-- Messages / Toasts in Modal -->
          <Transition name="fade">
            <div v-if="modalToast.show" :class="['modal-toast', modalToast.type]">
              <div class="toast-content">
                <span class="toast-icon">
                  <svg v-if="modalToast.type === 'success'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="20 6 9 17 4 12"></polyline>
                  </svg>
                  <svg v-else xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="8" x2="12" y2="12"></line>
                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                  </svg>
                </span>
                <div class="toast-text">
                  <h5 class="toast-title">{{ modalToast.title }}</h5>
                  <p class="toast-msg">{{ modalToast.message }}</p>
                </div>
              </div>
              <button @click="modalToast.show = false" class="toast-close" type="button">&times;</button>
            </div>
          </Transition>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, watch, computed, reactive } from 'vue'
import axios from 'axios'
import { useMicroServiceStore } from '../stores/microServices'
import { useAuthStore } from '../stores/auth'

const props = defineProps({
  productId: {
    type: [Number, String],
    default: null
  },
  show: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'inventory-updated'])

const microServices = useMicroServiceStore()
const authStore = useAuthStore()

// State
const product = ref(null)
const inventory = ref(null)
const isLoading = ref(false)
const isUpdating = ref(false)
const isPurchasing = ref(false)

const updateQty = ref(0)
const purchaseQty = ref(1)

const modalToast = reactive({
  show: false,
  type: 'success', // 'success' | 'error' | 'warning'
  title: '',
  message: ''
})

// Lifecycle/Watcher
watch(() => props.show, async (newVal) => {
  if (newVal && props.productId) {
    // Reset forms & status
    updateQty.value = 0
    purchaseQty.value = 1
    modalToast.show = false
    await fetchDetails()
  } else {
    product.value = null
    inventory.value = null
  }
})

// Fetch product & inventory details
const fetchDetails = async () => {
  isLoading.value = true
  product.value = null
  inventory.value = null

  try {
    // 1. Fetch Product
    const prodRes = await axios.get(`${microServices.prodcutsUrl}/api/productos/${props.productId}`, {
      headers: {
        'X-API-KEY': authStore.apiKey
      }
    })
    product.value = prodRes.data

    // 2. Fetch Inventory
    try {
      const invRes = await axios.get(`${microServices.inventoryUrl}/api/inventario/${props.productId}`, {
        headers: {
          'X-API-KEY': authStore.apiKey
        }
      })
      inventory.value = invRes.data
      updateQty.value = invRes.data.cantidad
    } catch (invErr) {
      if (invErr.response && invErr.response.status === 404) {
        // Product has no inventory record yet
        inventory.value = null // will trigger "Sin inventario inicial"
        updateQty.value = 0
      } else {
        throw invErr
      }
    }
  } catch (error) {
    console.error('Error fetching details in modal:', error)
    showToast('error', 'Error del Servidor', 'No se pudieron recuperar los datos. Asegúrese de que los microservicios estén activos.')
  } finally {
    isLoading.value = false
  }
}

// Update inventory stock (PUT)
const updateStock = async () => {
  modalToast.show = false
  isUpdating.value = true

  try {
    const response = await axios.put(
      `${microServices.inventoryUrl}/api/inventario/${props.productId}?cantidad=${updateQty.value}`,
      null,
      {
        headers: {
          'X-API-KEY': authStore.apiKey
        }
      }
    )
    inventory.value = response.data
    showToast('success', '¡Éxito!', `Inventario actualizado a ${updateQty.value} unidades.`)
    emit('inventory-updated', { productId: props.productId, cantidad: updateQty.value })
  } catch (error) {
    console.error('Error updating stock:', error)
    showToast('error', 'Error al Actualizar', 'No se pudo guardar la nueva cantidad en el microservicio de inventario.')
  } finally {
    isUpdating.value = false
  }
}

// Process Purchase (POST)
const processPurchase = async () => {
  modalToast.show = false
  
  if (inventory.value && purchaseQty.value > inventory.value.cantidad) {
    showToast('error', 'Cantidad inválida', 'La cantidad a comprar excede el stock disponible.')
    return
  }

  isPurchasing.value = true

  const payload = {
    productoId: Number(props.productId),
    cantidad: Number(purchaseQty.value)
  }

  try {
    const response = await axios.post(
      `${microServices.inventoryUrl}/api/inventario/compra`,
      payload,
      {
        headers: {
          'Content-Type': 'application/json',
          'X-API-KEY': authStore.apiKey
        }
      }
    )
    
    showToast('success', '¡Compra Exitosa!', response.data || 'Transacción registrada correctamente.')
    purchaseQty.value = 1
    
    // Refresh inventory data
    await refreshInventoryOnly()
    emit('inventory-updated', { productId: props.productId, cantidad: inventory.value.cantidad })
  } catch (error) {
    console.error('Error processing purchase:', error)
    let errorMsg = 'Error al comunicarse con el microservicio de inventario.'
    if (error.response && error.response.data) {
      errorMsg = error.response.data
    }
    showToast('error', 'Error en la Compra', errorMsg)
  } finally {
    isPurchasing.value = false
  }
}

const refreshInventoryOnly = async () => {
  try {
    const invRes = await axios.get(`${microServices.inventoryUrl}/api/inventario/${props.productId}`, {
      headers: {
        'X-API-KEY': authStore.apiKey
      }
    })
    inventory.value = invRes.data
    updateQty.value = invRes.data.cantidad
  } catch (err) {
    console.error('Error refreshing inventory status:', err)
  }
}

// Helpers
const closeModal = () => {
  emit('close')
}

const showToast = (type, title, message) => {
  modalToast.show = true
  modalToast.type = type
  modalToast.title = title
  modalToast.message = message
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    return date.toLocaleString('es-CO', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (e) {
    return dateStr
  }
}

// Computed Stock Classes
const stockStatusClass = computed(() => {
  if (!inventory.value) return 'unregistered'
  const qty = inventory.value.cantidad
  if (qty === 0) return 'danger'
  if (qty < 10) return 'warning'
  return 'success'
})
</script>

<style lang="scss" scoped>
/* Modal Overlay Background */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(15, 23, 42, 0.65);
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  padding: 20px;
}

/* Modal Card */
.modal-card {
  background: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.25);
  width: 100%;
  max-width: 650px;
  display: flex;
  flex-direction: column;
  max-height: 90vh;
  overflow: hidden;
  position: relative;
  transition: all 0.3s ease;

  @media (prefers-color-scheme: dark) {
    background: var(--color-background-soft);
    box-shadow: 0 25px 50px rgba(0, 0, 0, 0.4);
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

/* Header */
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px 16px;
  border-bottom: 1px solid var(--color-border);

  .header-title-container {
    display: flex;
    align-items: center;
    gap: 14px;
  }

  .header-icon {
    width: 44px;
    height: 44px;
    border-radius: 12px;
    background: rgba(65, 184, 131, 0.1);
    color: #41b883;
    display: flex;
    justify-content: center;
    align-items: center;

    svg {
      width: 22px;
      height: 22px;
    }
  }

  h3 {
    margin: 0;
    font-size: 1.35rem;
    font-weight: 700;
    color: var(--color-heading);
  }

  .subtitle {
    margin: 3px 0 0 0;
    font-size: 0.9rem;
    color: var(--color-text);
    opacity: 0.8;
  }

  .close-btn {
    background: none;
    border: none;
    font-size: 2rem;
    line-height: 1;
    color: var(--color-text);
    opacity: 0.6;
    cursor: pointer;
    transition: opacity 0.2s, transform 0.2s;
    padding: 0;
    width: 32px;
    height: 32px;
    display: flex;
    justify-content: center;
    align-items: center;

    &:hover {
      opacity: 1;
      transform: scale(1.1);
    }
  }
}

/* Body */
.modal-body {
  padding: 24px 28px 28px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Loading State */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  gap: 16px;
  color: var(--color-text);
  opacity: 0.8;

  p {
    font-size: 0.95rem;
  }
}

/* Product Info Summary */
.product-info-summary {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 10px;
  padding: 16px;

  .info-item {
    display: flex;
    flex-direction: column;
    gap: 4px;

    &.full {
      grid-column: 1 / -1;
      border-top: 1px solid var(--color-border);
      padding-top: 8px;
      margin-top: 4px;
    }

    .label {
      font-size: 0.75rem;
      font-weight: 600;
      color: var(--color-text);
      opacity: 0.6;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .value {
      font-size: 0.95rem;
      color: var(--color-heading);
      font-weight: 500;

      &.highlight {
        color: #41b883;
        font-weight: 700;
      }

      &.desc {
        font-size: 0.85rem;
        line-height: 1.4;
      }
    }
  }
}

/* Inventory Status Card */
.status-card {
  border-radius: 12px;
  padding: 18px 20px;
  border-left: 5px solid;
  transition: all 0.25s ease;

  .status-main {
    display: flex;
    align-items: center;
    gap: 18px;
  }

  .status-icon-wrapper {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    
    svg {
      width: 24px;
      height: 24px;
    }
  }

  .status-info {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .stock-title {
    font-size: 0.8rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    opacity: 0.9;
  }

  .stock-count {
    margin: 4px 0;
    font-size: 2rem;
    font-weight: 800;
    line-height: 1.1;

    .unit {
      font-size: 0.95rem;
      font-weight: 500;
      opacity: 0.8;
    }
  }

  .last-update {
    margin: 0;
    font-size: 0.75rem;
    opacity: 0.8;

    &.uninitialized {
      font-style: italic;
      opacity: 0.75;
    }
  }

  /* Color Schemes */
  &.success {
    background: rgba(16, 185, 129, 0.1);
    border-color: #10b981;
    color: #065f46;
    @media (prefers-color-scheme: dark) { color: #34d399; }
    .status-icon-wrapper { background: rgba(16, 185, 129, 0.2); color: #10b981; }
  }

  &.warning {
    background: rgba(245, 158, 11, 0.1);
    border-color: #f59e0b;
    color: #92400e;
    @media (prefers-color-scheme: dark) { color: #fbbf24; }
    .status-icon-wrapper { background: rgba(245, 158, 11, 0.2); color: #f59e0b; }
  }

  &.danger {
    background: rgba(239, 68, 68, 0.1);
    border-color: #ef4444;
    color: #991b1b;
    @media (prefers-color-scheme: dark) { color: #f87171; }
    .status-icon-wrapper { background: rgba(239, 68, 68, 0.2); color: #ef4444; }
  }

  &.unregistered {
    background: rgba(100, 116, 139, 0.1);
    border-color: #64748b;
    color: #334155;
    @media (prefers-color-scheme: dark) { color: #94a3b8; }
    .status-icon-wrapper { background: rgba(100, 116, 139, 0.2); color: #64748b; }
  }
}

/* Actions Grid */
.actions-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;

  @media (max-width: 580px) {
    grid-template-columns: 1fr;
  }
}

.action-card {
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 10px;
  padding: 18px;
  display: flex;
  flex-direction: column;
  gap: 10px;

  h4 {
    margin: 0;
    font-size: 0.95rem;
    font-weight: 700;
    color: var(--color-heading);
  }

  .action-desc {
    margin: 0;
    font-size: 0.8rem;
    color: var(--color-text);
    opacity: 0.7;
    line-height: 1.4;
    min-height: 38px;
  }
}

.action-form {
  margin-top: auto;
}

.input-group {
  display: flex;
  gap: 8px;

  input {
    flex: 1;
    width: 60px;
    padding: 10px 12px;
    font-size: 0.9rem;
    border: 1px solid var(--color-border);
    background: var(--color-background-soft);
    color: var(--color-text);
    border-radius: 6px;
    outline: none;
    transition: all 0.2s;

    &:focus {
      border-color: #41b883;
      box-shadow: 0 0 0 2px rgba(65, 184, 131, 0.15);
    }
  }

  .action-btn {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    gap: 6px;
    padding: 10px 14px;
    font-size: 0.85rem;
    font-weight: 600;
    color: white;
    background: #41b883;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.2s, opacity 0.2s;
    white-space: nowrap;

    &:hover:not(:disabled) {
      background: #3ba776;
    }

    &.purchase {
      background: #35495e;
      
      &:hover:not(:disabled) {
        background: #2c3e50;
      }
    }

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }
}

/* Spinner Utilities */
.spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 0.8s linear infinite;

  &.large {
    width: 32px;
    height: 32px;
    border-width: 3px;
    border-top-color: #41b883;
    border-bottom-color: transparent;
  }
}

/* Toast Message in Modal */
.modal-toast {
  padding: 12px 16px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
  margin-top: 10px;
  font-size: 0.85rem;
  border: 1px solid;

  &.success {
    background-color: rgba(16, 185, 129, 0.08);
    border-color: rgba(16, 185, 129, 0.2);
    color: #065f46;
    @media (prefers-color-scheme: dark) { color: #34d399; }
    .toast-icon { color: #10b981; }
  }

  &.error {
    background-color: rgba(239, 68, 68, 0.08);
    border-color: rgba(239, 68, 68, 0.2);
    color: #991b1b;
    @media (prefers-color-scheme: dark) { color: #f87171; }
    .toast-icon { color: #ef4444; }
  }

  .toast-content {
    display: flex;
    gap: 10px;
    flex: 1;
  }

  .toast-icon {
    display: flex;
    margin-top: 1px;
    svg {
      width: 16px;
      height: 16px;
    }
  }

  .toast-text {
    text-align: left;
  }

  .toast-title {
    margin: 0 0 2px 0;
    font-weight: 700;
  }

  .toast-msg {
    margin: 0;
    opacity: 0.9;
    line-height: 1.35;
  }

  .toast-close {
    background: none;
    border: none;
    font-size: 1.1rem;
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
}

/* Animations */
@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.animate-scale-in {
  animation: scaleIn 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
