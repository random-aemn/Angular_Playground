export class GetChart2DataDTO {

  public name: string;
  // have to account for the fact that there could be null values
  public data: (number | null) [];

}
