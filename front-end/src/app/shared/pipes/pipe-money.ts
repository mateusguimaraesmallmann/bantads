import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'money'
})
export class MoneyPipe implements PipeTransform {
  transform(value: number | string): string {
    if (value === null || value === undefined) return '0,00';

    let numericValue = typeof value === 'string'
      ? parseFloat(value.replace(/\./g, '').replace(',', '.'))
      : value;

    return numericValue.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    });
  }
}

