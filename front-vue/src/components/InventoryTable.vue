<template>
  <div class="inventory-table-container">
    <div class="table-card animate-fade-in">
      <!-- Table Header -->
      <div class="table-header">
        <div class="header-left">
          <div class="header-icon">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="8" y1="6" x2="21" y2="6"></line>
              <line x1="8" y1="12" x2="21" y2="12"></line>
              <line x1="8" y1="18" x2="21" y2="18"></line>
              <line x1="3" y1="6" x2="3.01" y2="6"></line>
              <line x1="3" y1="12" x2="3.01" y2="12"></line>
              <line x1="3" y1="18" x2="3.01" y2="18"></line>
            </svg>
          </div>
          <div>
            <h2>Catálogo Maestro de Productos</h2>
            <p class="subtitle">Consulte la lista de productos y haga clic en el botón de inventario para ver o modificar su stock.</p>
          </div>
        </div>

        <!-- Search Input -->
        <div class="search-wrapper">
          <div class="search-input-container">
            <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="Buscar por nombre o código..." 
              class="search-input"
            />
            <button v-if="searchQuery" @click="searchQuery = ''" class="clear-btn">&times;</button>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="table-loading">
        <span class="spinner"></span>
        <p>Cargando catálogo de productos...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="filteredProducts.length === 0" class="table-empty">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="8" y1="12" x2="16" y2="12"></line>
        </svg>
        <p v-if="products.length === 0">No hay productos registrados en la base de datos.</p>
        <p v-else>No se encontraron productos que coincidan con la búsqueda.</p>
      </div>

      <!-- Table Content -->
      <div v-else class="table-responsive">
        <table class="custom-table">
          <thead>
            <tr>
              <th class="col-id">ID</th>
              <th class="col-barcode">Código de Barras</th>
              <th class="col-name">Nombre</th>
              <th class="col-price">Precio</th>
              <th class="col-desc">Descripción</th>
              <th class="col-actions">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="prod in filteredProducts" :key="prod.id">
              <td class="col-id font-mono">{{ prod.id }}</td>
              <td class="col-barcode font-mono">
                <span class="barcode-badge">{{ prod.codigoBarras }}</span>
              </td>
              <td class="col-name"><strong>{{ prod.nombre }}</strong></td>
              <td class="col-price font-semibold text-green">
                ${{ Number(prod.precio).toLocaleString('es-CO', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) }}
              </td>
              <td class="col-desc">
                <p class="truncate-desc" :title="prod.descripcion">{{ prod.descripcion }}</p>
              </td>
              <td class="col-actions">
                <button 
                  class="action-btn" 
                  @click="openInventory(prod.id)"
                  title="Gestionar Stock"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="btn-icon">
                    <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
                    <polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline>
                    <line x1="12" y1="22.08" x2="12" y2="12"></line>
                  </svg>
                  <span>Ver Stock</span>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Error Toast -->
      <div v-if="errorToast.show" class="error-banner">
        <span>{{ errorToast.message }}</span>
        <button @click="fetchProducts" class="retry-btn">Reintentar</button>
      </div>
    </div>

    <!-- Inventory Modal Component -->
    <InventoryModal 
      :product-id="selectedProductId" 
      :show="showModal" 
      @close="showModal = false"
      @inventory-updated="handleInventoryUpdated"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import axios from 'axios'
import { useMicroServiceStore } from '../stores/microServices'
import { useAuthStore } from '../stores/auth'
import InventoryModal from './InventoryModal.vue'

const microServices = useMicroServiceStore()
const authStore = useAuthStore()

// State
const products = ref([])
const searchQuery = ref('')
const isLoading = ref(false)
const showModal = ref(false)
const selectedProductId = ref(null)

const errorToast = reactive({
  show: false,
  message: ''
})

// Fetch all products
const fetchProducts = async () => {
  isLoading.value = true
  errorToast.show = false
  try {
    const response = await axios.get(`${microServices.prodcutsUrl}/api/productos`, {
      headers: {
        'X-API-KEY': authStore.apiKey
      }
    })
    products.value = response.data
  } catch (error) {
    console.error('Error fetching products:', error)
    errorToast.show = true
    errorToast.message = 'No se pudo conectar con el microservicio de productos (puerto 8081). Verifique que esté en ejecución o que la API Key sea válida.'
  } finally {
    isLoading.value = false
  }
}

// Open inventory modal
const openInventory = (id) => {
  selectedProductId.value = id
  showModal.value = true
}

// Handle stock update in table (if we want to refresh table or just log)
const handleInventoryUpdated = (data) => {
  console.log(`Inventario del producto ${data.productId} actualizado a ${data.cantidad}`)
}

// Search Filter
const filteredProducts = computed(() => {
  if (!searchQuery.value) return products.value
  
  const query = searchQuery.value.toLowerCase().trim()
  return products.value.filter(prod => {
    const nameMatch = prod.nombre && prod.nombre.toLowerCase().includes(query)
    const codeMatch = prod.codigoBarras && prod.codigoBarras.toLowerCase().includes(query)
    return nameMatch || codeMatch
  })
})

onMounted(() => {
  fetchProducts()
})
</script>

<style lang="scss" scoped>
.inventory-table-container {
  width: 100%;
  padding: 20px;
  display: flex;
  justify-content: center;
}

.table-card {
  background: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  width: 100%;
  max-width: 1200px;
  padding: 30px;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: 20px;

  @media (prefers-color-scheme: dark) {
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
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

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 20px;

  .header-left {
    display: flex;
    align-items: center;
    gap: 15px;
    flex: 1;
    min-width: 280px;

    .header-icon {
      display: inline-flex;
      justify-content: center;
      align-items: center;
      width: 50px;
      height: 50px;
      background: rgba(65, 184, 131, 0.1);
      color: #41b883;
      border-radius: 12px;
      
      svg {
        width: 26px;
        height: 26px;
      }
    }

    h2 {
      color: var(--color-heading);
      font-size: 1.5rem;
      font-weight: 700;
      margin: 0;
    }

    .subtitle {
      color: var(--color-text);
      opacity: 0.75;
      font-size: 0.88rem;
      margin: 4px 0 0 0;
      line-height: 1.35;
    }
  }
}

/* Search Box styling */
.search-wrapper {
  min-width: 280px;
}

.search-input-container {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;

  .search-icon {
    position: absolute;
    left: 12px;
    width: 18px;
    height: 18px;
    color: var(--color-text);
    opacity: 0.5;
    pointer-events: none;
  }

  .search-input {
    width: 100%;
    padding: 10px 36px 10px 38px;
    font-size: 0.9rem;
    color: var(--color-text);
    background: var(--color-background);
    border: 1px solid var(--color-border);
    border-radius: 8px;
    outline: none;
    transition: all 0.25s;

    &:focus {
      border-color: #41b883;
      box-shadow: 0 0 0 3px rgba(65, 184, 131, 0.15);
    }
  }

  .clear-btn {
    position: absolute;
    right: 12px;
    background: none;
    border: none;
    font-size: 1.2rem;
    color: var(--color-text);
    opacity: 0.5;
    cursor: pointer;
    line-height: 1;
    padding: 0;

    &:hover {
      opacity: 1;
    }
  }
}

/* Table loading & empty states */
.table-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 50px 0;
  gap: 15px;
  color: var(--color-text);
  
  .spinner {
    width: 30px;
    height: 30px;
    border: 3px solid rgba(65, 184, 131, 0.2);
    border-radius: 50%;
    border-top-color: #41b883;
    animation: spin 0.8s linear infinite;
  }
}

.table-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
  gap: 12px;
  color: var(--color-text);
  opacity: 0.75;
  text-align: center;

  svg {
    width: 48px;
    height: 48px;
    color: var(--color-text);
    opacity: 0.4;
  }

  p {
    font-size: 0.95rem;
  }
}

/* Table Layout */
.table-responsive {
  width: 100%;
  overflow-x: auto;
  border-radius: 8px;
  border: 1px solid var(--color-border);
}

.custom-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  font-size: 0.92rem;

  thead {
    background: var(--color-background);
    
    th {
      padding: 14px 18px;
      font-weight: 600;
      color: var(--color-heading);
      border-bottom: 2px solid var(--color-border);
      text-transform: uppercase;
      font-size: 0.75rem;
      letter-spacing: 0.5px;
    }
  }

  tbody {
    tr {
      border-bottom: 1px solid var(--color-border);
      transition: background-color 0.2s;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background-color: rgba(65, 184, 131, 0.03);
      }
    }

    td {
      padding: 14px 18px;
      vertical-align: middle;
      color: var(--color-text);
    }
  }

  .col-id {
    width: 80px;
    color: var(--color-text);
    opacity: 0.7;
  }

  .col-barcode {
    width: 160px;
    
    .barcode-badge {
      background: var(--color-background-mute);
      padding: 4px 8px;
      border-radius: 4px;
      border: 1px solid var(--color-border);
      font-size: 0.8rem;
    }
  }

  .col-name {
    min-width: 200px;
    color: var(--color-heading);
  }

  .col-price {
    width: 130px;
    font-weight: 600;
  }

  .col-desc {
    min-width: 250px;
    
    .truncate-desc {
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      margin: 0;
      font-size: 0.85rem;
      opacity: 0.85;
      line-height: 1.4;
    }
  }

  .col-actions {
    width: 140px;
    text-align: right;
  }
}

/* Action button inside table */
.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 14px;
  font-size: 0.82rem;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, #41b883, #2d825c);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 6px rgba(65, 184, 131, 0.15);

  &:hover {
    background: linear-gradient(135deg, #4cd096, #35966b);
    transform: translateY(-1px);
    box-shadow: 0 4px 10px rgba(65, 184, 131, 0.25);
  }

  &:active {
    transform: translateY(1px);
  }

  .btn-icon {
    width: 14px;
    height: 14px;
  }
}

/* Error Banner styling */
.error-banner {
  background-color: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.2);
  color: #ef4444;
  padding: 14px 18px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.88rem;
  margin-top: 10px;

  .retry-btn {
    background: #ef4444;
    color: white;
    border: none;
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 0.8rem;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.2s;

    &:hover {
      background: #dc2626;
    }
  }
}

/* Spin animation */
@keyframes spin {
  to { transform: rotate(360deg); }
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
  animation: slideUp 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.font-mono {
  font-family: monospace;
}

.font-semibold {
  font-weight: 600;
}

.text-green {
  color: #41b883;
}
</style>
