import { Component } from '@angular/core';
import { Button } from 'primeng/button';
import { Tag } from 'primeng/tag';
import { Card } from 'primeng/card';
import { Divider } from 'primeng/divider';

@Component({
  selector: 'app-landing-page',
  imports: [
    Button,
    Tag,
    Card,
    Divider
  ],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.css',
})
export class LandingPage {
  protected readonly locations = [
    {
      title: 'Teren de tenis',
      description: 'Teren în aer liber, suprafață dură.',
      gradientClass: 'bg-gradient-to-br from-orange-400 via-rose-400 to-purple-300',
    },
    {
      title: 'Arena Cluj',
      description: 'Stadion, 30.000 de locuri.',
      gradientClass: 'bg-gradient-to-br from-sky-400 to-indigo-500',
    },
    {
      title: 'Parc',
      description: 'Zonă pentru alergat și plimbări.',
      gradientClass: 'bg-gradient-to-br from-emerald-400 to-teal-600',
    },
  ];
}
