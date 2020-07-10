export class LibraryFactory {
  static create({groupID = '', artifactID = '', version = ''}): Library {
    return new Library(groupID, artifactID, version);
  }
}

export interface ILibrary {
  groupID: string;
  artifactID: string;
  version: string;
}

export class Library implements ILibrary {
  private _groupID: string;
  private _artifactID: string;
  private _version: string;

  constructor(groupID: string, artifactID: string, version: string) {
    this._groupID = groupID;
    this._artifactID = artifactID;
    this._version = version;
  }

  get groupID(): string {
    return this._groupID;
  }

  set groupID(value: string) {
    this._groupID = value;
  }

  get artifactID(): string {
    return this._artifactID;
  }

  set artifactID(value: string) {
    this._artifactID = value;
  }

  get version(): string {
    return this._version;
  }

  set version(value: string) {
    this._version = value;
  }
}
