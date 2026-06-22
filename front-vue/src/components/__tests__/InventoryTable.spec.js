import { describe, it, expect, vi, beforeEach } from 'vitest';
import { mount } from '@vue/test-utils';
import { createPinia, setActivePinia } from 'pinia';
import InventoryTable from '../InventoryTable.vue';
import axios from 'axios';

// Mock axios
vi.mock('axios');

describe('InventoryTable.vue', () => {
  const mockProducts = [
    { id: 1, codigoBarras: 'BARCODE123', nombre: 'Detergente Líquido', precio: 12500, descripcion: 'Limpieza profunda' },
    { id: 2, codigoBarras: 'BARCODE456', nombre: 'Jabón Corporal', precio: 4500, descripcion: 'Aroma coco' }
  ];

  beforeEach(() => {
    setActivePinia(createPinia());
    vi.clearAllMocks();
  });

  it('renders loading state initially and then displays product list in a table', async () => {
    let resolveProducts;
    const productsPromise = new Promise(resolve => {
      resolveProducts = resolve;
    });
    
    axios.get.mockReturnValue(productsPromise);

    const wrapper = mount(InventoryTable);
    
    // Wait for Vue to flush the reactive DOM update from fetchProducts()'s isLoading = true
    await wrapper.vm.$nextTick();
    
    // Check loading message
    expect(wrapper.text()).toContain('Cargando catálogo de productos...');
    
    // Resolve the promise
    resolveProducts({ data: mockProducts });

    // Wait for the mounted lifecycle and axios mock to resolve
    await new Promise(resolve => setTimeout(resolve, 0));
    await wrapper.vm.$nextTick();

    expect(wrapper.text()).not.toContain('Cargando catálogo de productos...');
    expect(wrapper.text()).toContain('Detergente Líquido');
    expect(wrapper.text()).toContain('Jabón Corporal');
    expect(wrapper.text()).toContain('BARCODE123');
    expect(wrapper.text()).toContain('BARCODE456');
    expect(wrapper.findAll('tbody tr').length).toBe(2);
  });

  it('filters products correctly when search input is typed', async () => {
    axios.get.mockResolvedValue({ data: mockProducts });

    const wrapper = mount(InventoryTable);
    
    await new Promise(resolve => setTimeout(resolve, 0));
    await wrapper.vm.$nextTick();

    const searchInput = wrapper.find('.search-input');
    
    // Filter by name
    await searchInput.setValue('Jabón');
    expect(wrapper.text()).toContain('Jabón Corporal');
    expect(wrapper.text()).not.toContain('Detergente Líquido');
    
    // Filter by barcode
    await searchInput.setValue('BARCODE123');
    expect(wrapper.text()).toContain('Detergente Líquido');
    expect(wrapper.text()).not.toContain('Jabón Corporal');
  });

  it('opens inventory modal and sets selectedProductId when action button is clicked', async () => {
    axios.get.mockResolvedValue({ data: mockProducts });

    const wrapper = mount(InventoryTable);
    
    await new Promise(resolve => setTimeout(resolve, 0));
    await wrapper.vm.$nextTick();

    // Click the "Ver Stock" button on the first row
    const actionBtn = wrapper.find('.action-btn');
    expect(actionBtn.exists()).toBe(true);
    await actionBtn.trigger('click');

    expect(wrapper.vm.showModal).toBe(true);
    expect(wrapper.vm.selectedProductId).toBe(1);
  });
});
