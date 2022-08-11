import os
from os import getenv
from pathlib import Path

from ansible_vault import Vault


def get_environment():
    env_path = Path(__file__).parent.parent / "settings/environment.json"

    vault = Vault(getenv("TEST_VAULT_PASSWORD"))
    data = vault.load(env_path.read_text())
    return data
