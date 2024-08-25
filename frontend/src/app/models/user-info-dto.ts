export class UserInfoDTO {
  public loggedInUserId:    number;
  public loggedInUserName:  string;
  public loggedInFullName:  string;
  public pageRoutes:        Map<string, boolean>;
  public displayedRoleName: string;
}
