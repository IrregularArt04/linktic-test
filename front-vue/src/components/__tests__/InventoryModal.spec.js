import { describe, it, expect, vi, beforeEach } from 'vitest';
import { mount } from '@vue/test-utils';
import { createPinia, setActivePinia } from 'pinia';
import InventoryModal from '../InventoryModal.vue';
import axios from 'axios';

// Mock axios
vi.mock('axios');

describe('InventoryModal.vue', () => {
  const mockProduct = { 
    id: 1, 
    codigoBarras: 'BARCODE123', 
    nombre: 'Detergente Líquido', 
    precio: 12500, 
    descripcion: 'Limpieza profunda' 
  };
  
  const mockInventory = { 
    id: 10, 
    producto: 1, 
    cantidad: 25, 
    actualizadoEn: '2026-06-22T03:00:00' 
  };

  beforeEach(() => {
    setActivePinia(createPinia());
    vi.clearAllMocks();
  });

  it('does not display the modal backdrop when show is false', () => {
    const wrapper = mount(InventoryModal, {
      props: {
        productId: 1,
        show: false
      }
    });

    expect(wrapper.find('.modal-backdrop').exists()).toBe(false);
  });

  it('displays loading state and then fetches and renders details when show becomes true', async () => {
    let resolveProduct, resolveInventory;
    const productPromise = new Promise(resolve => { resolveProduct = resolve; });
    const inventoryPromise = new Promise(resolve => { resolveInventory = resolve; });

    axios.get.mockImplementation((url) => {
      if (url.includes('/api/productos/1')) {
        return productPromise;
      }
      if (url.includes('/api/inventario/1')) {
        return inventoryPromise;
      }
      return Promise.reject(new Error('URL desconocida: ' + url));
    });

    const wrapper = mount(InventoryModal, {
      props: {
        productId: 1,
        show: false
      }
    });

    // Change show to true to trigger watch
    await wrapper.setProps({ show: true });

    expect(wrapper.find('.modal-backdrop').exists()).toBe(true);
    expect(wrapper.text()).toContain('Obteniendo datos del servidor...');

    // Resolve promises
    resolveProduct({ data: mockProduct });
    resolveInventory({ data: mockInventory });

    // Wait for the watchers & mock APIs to resolve
    await new Promise(resolve => setTimeout(resolve, 0));
    await wrapper.vm.$nextTick();

    expect(wrapper.text()).not.toContain('Obteniendo datos del servidor...');
    expect(wrapper.text()).toContain('Detergente Líquido');
    expect(wrapper.text()).toContain('BARCODE123');
    expect(wrapper.text()).toContain('25 unidades');
  });

  it('handles 404 response on inventory query gracefully', async () => {
    axios.get.mockImplementation((url) => {
      if (url.includes('/api/productos/1')) {
        return Promise.resolve({ data: mockProduct });
      }
      if (url.includes('/api/inventario/1')) {
        const error = new Error('Not Found');
        error.response = { status: 404 };
        return Promise.reject(error);
      }
      return Promise.reject(new Error('URL desconocida: ' + url));
    });

    const wrapper = mount(InventoryModal, {
      props: {
        productId: 1,
        show: false
      }
    });

    // Change show to true to trigger watch
    await wrapper.setProps({ show: true });

    await new Promise(resolve => setTimeout(resolve, 0));
    await wrapper.vm.$nextTick();

    expect(wrapper.text()).toContain('Sin inventario inicial registrado');
    expect(wrapper.text()).toContain('0 unidades');
  });

  it('submits a PUT request to update the stock correctly', async () => {
    axios.get.mockImplementation((url) => {
      if (url.includes('/api/productos/1')) {
        return Promise.resolve({ data: mockProduct });
      }
      if (url.includes('/api/inventario/1')) {
        return Promise.resolve({ data: mockInventory });
      }
      return Promise.reject(new Error('URL desconocida: ' + url));
    });

    axios.put.mockResolvedValue({ data: { ...mockInventory, cantidad: 80 } });

    const wrapper = mount(InventoryModal, {
      props: {
        productId: 1,
        show: false
      }
    });

    // Change show to true to trigger watch
    await wrapper.setProps({ show: true });

    await new Promise(resolve => setTimeout(resolve, 0));
    await wrapper.vm.$nextTick();

    // Set input value to 80
    const input = wrapper.find('input[placeholder="Nueva cantidad"]');
    expect(input.exists()).toBe(true);
    await input.setValue(80);

    // Submit "Fijar Cantidad" form
    const form = wrapper.find('.actions-grid .action-card:first-child form');
    await form.trigger('submit.prevent');

    expect(axios.put).toHaveBeenCalledWith(
      expect.stringContaining('/api/inventario/1?cantidad=80'),
      null,
      expect.objectContaining({
        headers: expect.objectContaining({
          'X-API-KEY': expect.any(String)
        })
      })
    );

    await new Promise(resolve => setTimeout(resolve, 0));
    await wrapper.vm.$nextTick();

    expect(wrapper.text()).toContain('Inventario actualizado a 80 unidades.');
  });
});
