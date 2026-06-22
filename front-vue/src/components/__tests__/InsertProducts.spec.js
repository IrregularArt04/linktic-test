import { describe, it, expect, vi, beforeEach } from 'vitest';
import { mount } from '@vue/test-utils';
import { createPinia, setActivePinia } from 'pinia';
import InsertProducts from '../InsertProducts.vue';
import axios from 'axios';

// Mock axios
vi.mock('axios');

describe('InsertProducts.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  it('renders all form fields correctly', () => {
    const wrapper = mount(InsertProducts);
    
    expect(wrapper.find('label[for="codigoBarras"]').text()).toContain('Código de Barras');
    expect(wrapper.find('input#codigoBarras').exists()).toBe(true);
    
    expect(wrapper.find('label[for="nombre"]').text()).toContain('Nombre del Producto');
    expect(wrapper.find('input#nombre').exists()).toBe(true);
    
    expect(wrapper.find('label[for="precio"]').text()).toContain('Precio');
    expect(wrapper.find('input#precio').exists()).toBe(true);
    
    expect(wrapper.find('label[for="descripcion"]').text()).toContain('Descripción');
    expect(wrapper.find('textarea#descripcion').exists()).toBe(true);
    
    expect(wrapper.find('button[type="submit"]').text()).toContain('Guardar Producto');
  });

  it('validates fields and shows error messages on empty submission', async () => {
    const wrapper = mount(InsertProducts);
    
    // Submit the form immediately
    await wrapper.find('form').trigger('submit.prevent');
    
    // Check that error classes or error messages are shown
    expect(wrapper.find('.has-error').exists()).toBe(true);
    expect(wrapper.text()).toContain('El código de barras es obligatorio.');
    expect(wrapper.text()).toContain('El nombre del producto es obligatorio.');
    expect(wrapper.text()).toContain('El precio es obligatorio.');
    expect(wrapper.text()).toContain('La descripción es obligatoria.');
  });

  it('validates barcode format and length', async () => {
    const wrapper = mount(InsertProducts);
    const barcodeInput = wrapper.find('input#codigoBarras');
    
    // Invalid characters (spaces and symbols)
    await barcodeInput.setValue('INVALID BARCODE!');
    await barcodeInput.trigger('blur');
    expect(wrapper.text()).toContain('El código de barras debe ser alfanumérico');
    
    // Too long
    await barcodeInput.setValue('A'.repeat(26));
    await barcodeInput.trigger('blur');
    expect(wrapper.text()).toContain('El código de barras no puede superar los 25 caracteres.');
    
    // Correct format
    await barcodeInput.setValue('VALID123');
    await barcodeInput.trigger('blur');
    expect(wrapper.find('.form-group:nth-child(1)').classes()).toContain('is-valid');
  });

  it('validates price value and decimal precision', async () => {
    const wrapper = mount(InsertProducts);
    const priceInput = wrapper.find('input#precio');
    
    // Negative price
    await priceInput.setValue(-10.5);
    await priceInput.trigger('blur');
    expect(wrapper.text()).toContain('El precio debe ser un valor mayor a 0.');
    
    // More than 2 decimal places
    await priceInput.setValue(10.555);
    await priceInput.trigger('blur');
    expect(wrapper.text()).toContain('El precio debe tener como máximo 10 dígitos enteros y 2 decimales');
    
    // Too many integer digits
    await priceInput.setValue(12345678901.5);
    await priceInput.trigger('blur');
    expect(wrapper.text()).toContain('El precio debe tener como máximo 10 dígitos enteros y 2 decimales');
    
    // Valid price
    await priceInput.setValue(99.99);
    await priceInput.trigger('blur');
    expect(wrapper.find('.form-group:nth-child(3)').classes()).toContain('is-valid');
  });
});
