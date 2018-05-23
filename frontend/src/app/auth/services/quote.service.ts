import { Injectable } from '@angular/core';
import { AuthModule } from '../auth.module';
import { of, Observable } from 'rxjs';
import { Quote } from '../../domain/quote';

@Injectable({
  providedIn: 'root'
})
export class QuoteService {
  constructor() {}

  getQuotes(): Observable<Quote[]> {
    const quotes: Quote[] = [
      {
        cn:
          '我突然就觉得自己像个华丽的木偶,演尽了所有的悲欢离合,可是背上总是有无数闪亮的银色丝线,操纵我哪怕一举手一投足。',
        en:
          'I suddenly feel myself like a doll,acting all kinds of joys and sorrows.There are lots of shining silvery thread on my back,controlling all my action.',
        imgUrl: '/assets/img/quotes/0.jpg'
      },
      {
        cn:
          '被击垮通常只是暂时的，但如果你放弃的话，就会使它成为永恒。（Marilyn vos Savant）',
        en:
          'Being defeated is often a temporary condition. Giving up is what makes it permanent.',
        imgUrl: '/assets/img/quotes/1.jpg'
      },
      {
        cn: '不要只因一次挫败，就放弃你原来决心想达到的梦想。（莎士比亚）',
        en:
          'Do not, for one repulse, forgo the purpose that you resolved to effect.',
        imgUrl: '/assets/img/quotes/2.jpg'
      },
      {
        cn: '想有发现就要实验，这项实验需要时间。—《神盾局特工》',
        en:
          'Discovery requires experimentation, and this experiment will take time.',
        imgUrl: '/assets/img/quotes/3.jpg'
      },
      {
        cn:
          '这世界并不会在意你的自尊，这世界希望你在自我感觉良好之前先要有所成就。',
        en:
          "The world won't care about your self-esteem. The world will expect you to accomplish something before you feel good about yourself.",
        imgUrl: '/assets/img/quotes/4.jpg'
      },
      {
        cn: '当你最终放开了过去，更好的事就会到来。',
        en:
          'When you finally let go of the past, something better comes along.',
        imgUrl: '/assets/img/quotes/5.jpg'
      },
      {
        cn:
          '我们学着放开过去伤害我们的人和事，学着只向前看。因为生活本来就是一往直前的。',
        en:
          'We learn to let go of things and people that hurt us in the past and just move on. For life is all about moving on.',
        imgUrl: '/assets/img/quotes/6.jpg'
      },
      {
        cn:
          '绝不要因为怕辛苦而拒绝一个想法、梦想或是目标，成功的路上难免伴随辛苦。（Bob Proctor）',
        en:
          'Never reject an idea, dream or goal because it will be hard work. Success rarely comes without it.',
        imgUrl: '/assets/img/quotes/7.jpg'
      },
      {
        cn:
          '我们在人生中会作出许多选择，带着这些选择继续生活，才是人生中最难的一课。《妙笔生花》',
        en:
          'We all make our choices in life. The hard thing to do is live with them.',
        imgUrl: '/assets/img/quotes/8.jpg'
      },
      {
        cn:
          '我总是对新的一天充满喜悦，这是一次新的尝试、一个新的开始，翘首以待，黎明之后或是惊喜。（约翰·博因顿·普里斯特利）',
        en:
          'I have always been delighted at the prospect of a new day, a fresh try, one more start, with perhaps a bit of magic waiting somewhere behind the morning.',
        imgUrl: '/assets/img/quotes/9.jpg'
      }
    ];
    return of(quotes);
  }
}
