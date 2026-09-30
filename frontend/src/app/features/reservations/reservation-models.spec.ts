import { overlaps } from './reservation-models';

function at(hour: number): string {
  const date = new Date();
  date.setHours(hour, 0, 0, 0);

  return date.toISOString();
}

describe('overlaps', () => {
  it('treats periods as half-open, so back-to-back bookings do not clash', () => {
    expect(overlaps(at(10), at(12), at(12), at(14))).toBe(false);
  });

  it('catches a period that starts inside another', () => {
    expect(overlaps(at(10), at(12), at(11), at(13))).toBe(true);
  });

  it('catches a period that contains another', () => {
    expect(overlaps(at(9), at(15), at(11), at(12))).toBe(true);
  });
});
