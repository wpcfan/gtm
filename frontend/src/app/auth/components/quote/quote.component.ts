import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-quote',
  templateUrl: './quote.component.html',
  styleUrls: ['./quote.component.scss']
})
export class QuoteComponent {
  @Input() title = '佳句';
  @Input() chineseQuote = '';
  @Input() englishQuote = '';
  @Input() quoteImg = '';
}
